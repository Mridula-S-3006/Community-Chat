let currentGroupId = null;
let currentGroupName = null;

document.addEventListener('DOMContentLoaded', () => {
    if (!requireAuth()) return;

    loadGroups();
    connectWebSocket();

    const sendButton = document.getElementById('send-group-message-btn');
    const messageInput = document.getElementById('group-message-input');
    const createGroupBtn = document.getElementById('create-group-btn');
    const searchInput = document.getElementById('member-search');
    const rsvpButtons = document.querySelectorAll('.rsvp-btn');

    if (sendButton) {
        sendButton.addEventListener('click', sendGroupMessage);
    }

    if (messageInput) {
        messageInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendGroupMessage();
            }
        });
    }

    if (createGroupBtn) {
        createGroupBtn.addEventListener('click', showCreateGroupModal);
    }

    if (searchInput) {
        searchInput.addEventListener('input', debounce(searchUsers, 300));
    }

    rsvpButtons.forEach(btn => {
        btn.addEventListener('click', handleRSVP);
    });
});

window.onWebSocketConnected = () => {
    if (currentGroupId) {
        subscribeToGroup(currentGroupId, handleIncomingGroupMessage);
    }
};

async function loadGroups() {
    try {
        const groups = await groupAPI.getAllGroups();
        displayGroups(groups);
    } catch (error) {
        console.error('Failed to load groups:', error);
    }
}

function displayGroups(groups) {
    const groupsList = document.getElementById('groups-list');
    if (!groupsList) return;

    groupsList.innerHTML = '';

    if (!groups || groups.length === 0) {
        groupsList.innerHTML = '<p class="text-center text-gray-500 p-4">No groups yet. Create one!</p>';
        return;
    }

    groups.forEach(group => {
        const groupEl = document.createElement('div');
        groupEl.className = 'group-item p-4 border-b cursor-pointer hover:bg-gray-100';
        groupEl.innerHTML = `
            <div class="flex items-center justify-between">
                <div>
                    <p class="font-semibold">${escapeHtml(group.name)}</p>
                    <p class="text-sm text-gray-600">${group.memberCount || 0} members</p>
                </div>
            </div>
        `;
        groupEl.addEventListener('click', () => openGroup(group.id, group.name));
        groupsList.appendChild(groupEl);
    });
}

async function openGroup(groupId, groupName) {
    if (currentGroupId) {
        unsubscribe(`group-${currentGroupId}`);
    }

    currentGroupId = groupId;
    currentGroupName = groupName;

    const groupHeader = document.getElementById('group-header');
    if (groupHeader) {
        groupHeader.innerHTML = `<h2 class="text-xl font-bold">${escapeHtml(groupName)}</h2>`;
    }

    document.getElementById('group-chat-area')?.classList.remove('hidden');

    subscribeToGroup(groupId, handleIncomingGroupMessage);

    try {
        const messages = await groupAPI.getMessages(groupId);
        displayGroupMessages(messages);

        const group = await groupAPI.getGroup(groupId);
        displayGroupEvents(group.events);
    } catch (error) {
        console.error('Failed to load group data:', error);
    }
}

function displayGroupMessages(messages) {
    const messagesContainer = document.getElementById('group-messages-container');
    if (!messagesContainer) return;

    messagesContainer.innerHTML = '';

    const currentUser = getCurrentUser();

    messages.forEach(msg => {
        const messageEl = document.createElement('div');
        const isSent = msg.senderId === currentUser.id;
        
        messageEl.className = `message mb-4 ${isSent ? 'text-right' : 'text-left'}`;
        messageEl.innerHTML = `
            <div class="inline-block max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${isSent ? 'bg-primary text-black' : 'bg-gray-200 text-black'}">
                ${!isSent ? `<p class="text-xs font-semibold mb-1">${escapeHtml(msg.senderUsername)}</p>` : ''}
                <p>${escapeHtml(msg.content)}</p>
                <p class="text-xs mt-1 opacity-75">${formatTime(msg.timestamp)}</p>
            </div>
        `;
        messagesContainer.appendChild(messageEl);
    });

    scrollToBottom('group-messages-container');
}

function displayGroupEvents(events) {
    const eventsContainer = document.getElementById('group-events-container');
    if (!eventsContainer || !events || events.length === 0) return;

    eventsContainer.innerHTML = '<h3 class="text-lg font-bold mb-4">Upcoming Events</h3>';

    events.forEach(event => {
        const eventEl = document.createElement('div');
        eventEl.className = 'event-card p-4 bg-white rounded-lg border mb-4';
        eventEl.innerHTML = `
            <h4 class="font-bold">${escapeHtml(event.title)}</h4>
            <p class="text-sm text-gray-600">${formatDate(event.dateTime)}</p>
            <p class="text-sm mt-2">${escapeHtml(event.description)}</p>
            <div class="mt-4 flex gap-2">
                <button class="rsvp-btn px-3 py-1 rounded text-sm bg-green-500 text-white" data-event-id="${event.id}" data-status="ATTENDING">Going</button>
                <button class="rsvp-btn px-3 py-1 rounded text-sm bg-red-500 text-white" data-event-id="${event.id}" data-status="NOT_ATTENDING">Not Going</button>
                <button class="rsvp-btn px-3 py-1 rounded text-sm bg-yellow-500 text-white" data-event-id="${event.id}" data-status="MAYBE">Maybe</button>
            </div>
            <div id="rsvp-stats-${event.id}" class="mt-2 text-sm text-gray-600"></div>
        `;
        
        eventEl.querySelectorAll('.rsvp-btn').forEach(btn => {
            btn.addEventListener('click', handleRSVP);
        });
        
        eventsContainer.appendChild(eventEl);
        loadRSVPStats(event.id);
    });
}

async function handleRSVP(e) {
    const eventId = e.target.dataset.eventId;
    const status = e.target.dataset.status;

    try {
        await eventAPI.rsvpEvent(eventId, status);
        showSuccess('RSVP updated!');
        loadRSVPStats(eventId);
    } catch (error) {
        console.error('RSVP failed:', error);
        showError('Failed to update RSVP');
    }
}

async function loadRSVPStats(eventId) {
    try {
        const rsvps = await eventAPI.getEventRsvps(eventId);
        const statsEl = document.getElementById(`rsvp-stats-${eventId}`);
        
        if (statsEl && rsvps) {
            const attending = rsvps.filter(r => r.status === 'ATTENDING').length;
            const notAttending = rsvps.filter(r => r.status === 'NOT_ATTENDING').length;
            const maybe = rsvps.filter(r => r.status === 'MAYBE').length;
            
            statsEl.innerHTML = `Going: ${attending} | Not Going: ${notAttending} | Maybe: ${maybe}`;
        }
    } catch (error) {
        console.error('Failed to load RSVP stats:', error);
    }
}

async function sendGroupMessage() {
    const messageInput = document.getElementById('group-message-input');
    const content = messageInput?.value.trim();

    if (!content || !currentGroupId) return;

    try {
        sendGroupMessage(currentGroupId, content);
        
        addGroupMessageToUI({
            senderId: getCurrentUser().id,
            senderUsername: getCurrentUser().username,
            content: content,
            timestamp: new Date().toISOString()
        }, true);

        messageInput.value = '';
    } catch (error) {
        console.error('Failed to send message:', error);
        showError('Failed to send message');
    }
}

function handleIncomingGroupMessage(message) {
    addGroupMessageToUI(message, message.senderId === getCurrentUser().id);
}

function addGroupMessageToUI(message, isSent) {
    const messagesContainer = document.getElementById('group-messages-container');
    if (!messagesContainer) return;

    const messageEl = document.createElement('div');
    messageEl.className = `message mb-4 ${isSent ? 'text-right' : 'text-left'}`;
    messageEl.innerHTML = `
        <div class="inline-block max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${isSent ? 'bg-primary text-black' : 'bg-gray-200 text-black'}">
            ${!isSent ? `<p class="text-xs font-semibold mb-1">${escapeHtml(message.senderUsername)}</p>` : ''}
            <p>${escapeHtml(message.content)}</p>
            <p class="text-xs mt-1 opacity-75">${formatTime(message.timestamp)}</p>
        </div>
    `;
    messagesContainer.appendChild(messageEl);
    scrollToBottom('group-messages-container');
}

function showCreateGroupModal() {
    const modal = document.getElementById('create-group-modal');
    if (modal) {
        modal.classList.remove('hidden');
    }
}

function hideCreateGroupModal() {
    const modal = document.getElementById('create-group-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

async function createGroup() {
    const groupName = document.getElementById('new-group-name')?.value.trim();
    const selectedMembers = Array.from(document.querySelectorAll('.selected-member')).map(el => el.dataset.userId);

    if (!groupName) {
        showError('Please enter a group name');
        return;
    }

    try {
        const newGroup = await groupAPI.createGroup({
            name: groupName,
            memberIds: selectedMembers
        });

        showSuccess('Group created successfully!');
        hideCreateGroupModal();
        loadGroups();
        openGroup(newGroup.id, newGroup.name);
    } catch (error) {
        console.error('Failed to create group:', error);
        showError('Failed to create group');
    }
}

async function searchUsers(query) {
    if (!query || query.length < 2) {
        document.getElementById('member-search-results')?.classList.add('hidden');
        return;
    }

    try {
        const users = await userAPI.searchUsers(query);
        displayMemberSearchResults(users);
    } catch (error) {
        console.error('Search failed:', error);
    }
}

function displayMemberSearchResults(users) {
    const searchResults = document.getElementById('member-search-results');
    if (!searchResults) return;

    searchResults.innerHTML = '';

    if (users.length === 0) {
        searchResults.innerHTML = '<p class="p-4 text-gray-500">No users found</p>';
        searchResults.classList.remove('hidden');
        return;
    }

    users.forEach(user => {
        const userEl = document.createElement('div');
        userEl.className = 'p-4 hover:bg-gray-100 cursor-pointer border-b';
        userEl.innerHTML = `
            <p class="font-semibold">${escapeHtml(user.username)}</p>
            <p class="text-sm text-gray-600">${escapeHtml(user.email)}</p>
        `;
        userEl.addEventListener('click', () => addMemberToSelection(user));
        searchResults.appendChild(userEl);
    });

    searchResults.classList.remove('hidden');
}

function addMemberToSelection(user) {
    const selectedContainer = document.getElementById('selected-members');
    if (!selectedContainer) return;

    if (document.querySelector(`.selected-member[data-user-id="${user.id}"]`)) {
        return;
    }

    const memberEl = document.createElement('div');
    memberEl.className = 'selected-member inline-block bg-primary text-black px-3 py-1 rounded-full mr-2 mb-2';
    memberEl.dataset.userId = user.id;
    memberEl.innerHTML = `
        ${escapeHtml(user.username)}
        <button class="ml-2 font-bold" onclick="this.parentElement.remove()">×</button>
    `;
    selectedContainer.appendChild(memberEl);
}

function scrollToBottom(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.scrollTop = container.scrollHeight;
    }
}

function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}

function formatDate(dateTime) {
    const date = new Date(dateTime);
    return date.toLocaleString('en-US', { 
        month: 'short', 
        day: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit' 
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

window.addEventListener('beforeunload', () => {
    disconnectWebSocket();
});