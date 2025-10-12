let currentChatUserId = null;
let currentChatUsername = null;

document.addEventListener('DOMContentLoaded', () => {
    if (!requireAuth()) return;

    loadConversations();
    connectWebSocket();

    const sendButton = document.getElementById('send-message-btn');
    const messageInput = document.getElementById('message-input');
    const searchInput = document.getElementById('user-search');

    if (sendButton) {
        sendButton.addEventListener('click', sendMessage);
    }

    if (messageInput) {
        messageInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }

    if (searchInput) {
        searchInput.addEventListener('input', debounce(searchUsers, 300));
    }
});

window.onWebSocketConnected = () => {
    const currentUser = getCurrentUser();
    if (currentUser) {
        subscribeToChat(currentUser.id, handleIncomingMessage);
    }
};

async function loadConversations() {
    try {
        const conversations = await messageAPI.getConversations();
        displayConversations(conversations);
    } catch (error) {
        console.error('Failed to load conversations:', error);
    }
}

function displayConversations(conversations) {
    const conversationsList = document.getElementById('conversations-list');
    if (!conversationsList) return;

    conversationsList.innerHTML = '';

    if (!conversations || conversations.length === 0) {
        conversationsList.innerHTML = '<p class="text-center text-gray-500 p-4">No conversations yet</p>';
        return;
    }

    conversations.forEach(conv => {
        const convEl = document.createElement('div');
        convEl.className = 'conversation-item p-4 border-b cursor-pointer hover:bg-gray-100';
        convEl.innerHTML = `
            <div class="flex items-center justify-between">
                <div>
                    <p class="font-semibold">${conv.username}</p>
                    <p class="text-sm text-gray-600 truncate">${conv.lastMessage || 'No messages'}</p>
                </div>
                ${conv.unreadCount ? `<span class="bg-primary text-white text-xs px-2 py-1 rounded-full">${conv.unreadCount}</span>` : ''}
            </div>
        `;
        convEl.addEventListener('click', () => openChat(conv.userId, conv.username));
        conversationsList.appendChild(convEl);
    });
}

async function openChat(userId, username) {
    currentChatUserId = userId;
    currentChatUsername = username;

    const chatHeader = document.getElementById('chat-header');
    if (chatHeader) {
        chatHeader.innerHTML = `<h2 class="text-xl font-bold">${username}</h2>`;
    }

    document.getElementById('chat-area')?.classList.remove('hidden');

    try {
        const messages = await messageAPI.getMessages(userId);
        displayMessages(messages);
    } catch (error) {
        console.error('Failed to load messages:', error);
    }
}

function displayMessages(messages) {
    const messagesContainer = document.getElementById('messages-container');
    if (!messagesContainer) return;

    messagesContainer.innerHTML = '';

    const currentUser = getCurrentUser();

    messages.forEach(msg => {
        const messageEl = document.createElement('div');
        const isSent = msg.senderId === currentUser.id;
        
        messageEl.className = `message mb-4 ${isSent ? 'text-right' : 'text-left'}`;
        messageEl.innerHTML = `
            <div class="inline-block max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${isSent ? 'bg-primary text-black' : 'bg-gray-200 text-black'}">
                <p>${escapeHtml(msg.content)}</p>
                <p class="text-xs mt-1 opacity-75">${formatTime(msg.timestamp)}</p>
            </div>
        `;
        messagesContainer.appendChild(messageEl);
    });

    scrollToBottom();
}

async function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const content = messageInput?.value.trim();

    if (!content || !currentChatUserId) return;

    try {
        sendChatMessage(currentChatUserId, content);
        
        addMessageToUI({
            senderId: getCurrentUser().id,
            content: content,
            timestamp: new Date().toISOString()
        }, true);

        messageInput.value = '';
    } catch (error) {
        console.error('Failed to send message:', error);
        showError('Failed to send message');
    }
}

function handleIncomingMessage(message) {
    if (message.senderId === currentChatUserId) {
        addMessageToUI(message, false);
    }
    
    loadConversations();
}

function addMessageToUI(message, isSent) {
    const messagesContainer = document.getElementById('messages-container');
    if (!messagesContainer) return;

    const messageEl = document.createElement('div');
    messageEl.className = `message mb-4 ${isSent ? 'text-right' : 'text-left'}`;
    messageEl.innerHTML = `
        <div class="inline-block max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${isSent ? 'bg-primary text-black' : 'bg-gray-200 text-black'}">
            <p>${escapeHtml(message.content)}</p>
            <p class="text-xs mt-1 opacity-75">${formatTime(message.timestamp)}</p>
        </div>
    `;
    messagesContainer.appendChild(messageEl);
    scrollToBottom();
}

async function searchUsers(query) {
    if (!query || query.length < 2) {
        document.getElementById('search-results')?.classList.add('hidden');
        return;
    }

    try {
        const users = await userAPI.searchUsers(query);
        displaySearchResults(users);
    } catch (error) {
        console.error('Search failed:', error);
    }
}

function displaySearchResults(users) {
    const searchResults = document.getElementById('search-results');
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
            <p class="font-semibold">${user.username}</p>
            <p class="text-sm text-gray-600">${user.email}</p>
        `;
        userEl.addEventListener('click', () => {
            openChat(user.id, user.username);
            searchResults.classList.add('hidden');
            document.getElementById('user-search').value = '';
        });
        searchResults.appendChild(userEl);
    });

    searchResults.classList.remove('hidden');
}

function scrollToBottom() {
    const messagesContainer = document.getElementById('messages-container');
    if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
}

function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
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