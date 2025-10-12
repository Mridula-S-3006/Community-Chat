let selectedGroupId = null;
let editingEventId = null;

document.addEventListener('DOMContentLoaded', () => {
    if (!requireAuth()) return;

    loadUserEvents();
    loadUserGroups();

    const createEventBtn = document.getElementById('create-event-btn');
    const eventForm = document.getElementById('event-form');
    const cancelBtn = document.getElementById('cancel-event-btn');
    const filterSelect = document.getElementById('group-filter');

    if (createEventBtn) {
        createEventBtn.addEventListener('click', showEventForm);
    }

    if (eventForm) {
        eventForm.addEventListener('submit', handleEventSubmit);
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', hideEventForm);
    }

    if (filterSelect) {
        filterSelect.addEventListener('change', (e) => {
            filterEventsByGroup(e.target.value);
        });
    }
});

async function loadUserEvents() {
    try {
        const events = await eventAPI.getUserEvents();
        displayEvents(events);
    } catch (error) {
        console.error('Failed to load events:', error);
        showError('Failed to load events');
    }
}

async function loadUserGroups() {
    try {
        const groups = await groupAPI.getAllGroups();
        populateGroupDropdowns(groups);
    } catch (error) {
        console.error('Failed to load groups:', error);
    }
}

function populateGroupDropdowns(groups) {
    const groupSelect = document.getElementById('event-group-select');
    const filterSelect = document.getElementById('group-filter');

    if (groupSelect) {
        groupSelect.innerHTML = '<option value="">Select a group</option>';
        groups.forEach(group => {
            const option = document.createElement('option');
            option.value = group.id;
            option.textContent = group.name;
            groupSelect.appendChild(option);
        });
    }

    if (filterSelect) {
        filterSelect.innerHTML = '<option value="">All Groups</option>';
        groups.forEach(group => {
            const option = document.createElement('option');
            option.value = group.id;
            option.textContent = group.name;
            filterSelect.appendChild(option);
        });
    }
}

function displayEvents(events) {
    const eventsContainer = document.getElementById('events-list');
    if (!eventsContainer) return;

    eventsContainer.innerHTML = '';

    if (!events || events.length === 0) {
        eventsContainer.innerHTML = '<p class="text-center text-gray-500 p-8">No events yet. Create one!</p>';
        return;
    }

    events.forEach(event => {
        const eventEl = document.createElement('div');
        eventEl.className = 'event-card p-6 bg-white rounded-lg border mb-4 hover:shadow-lg transition';
        eventEl.dataset.groupId = event.groupId;
        eventEl.innerHTML = `
            <div class="flex justify-between items-start">
                <div class="flex-1">
                    <div class="flex items-center gap-2 mb-2">
                        <h3 class="text-xl font-bold">${escapeHtml(event.title)}</h3>
                        <span class="text-xs bg-primary text-black px-2 py-1 rounded">${escapeHtml(event.groupName)}</span>
                    </div>
                    <p class="text-gray-600 mb-2">${formatDateTime(event.dateTime)}</p>
                    ${event.location ? `<p class="text-sm text-gray-500 mb-2">📍 ${escapeHtml(event.location)}</p>` : ''}
                    <p class="text-gray-700 mt-3">${escapeHtml(event.description)}</p>
                </div>
                <div class="flex gap-2 ml-4">
                    <button class="edit-event-btn px-3 py-1 bg-blue-500 text-white rounded text-sm" data-event-id="${event.id}">Edit</button>
                    <button class="delete-event-btn px-3 py-1 bg-red-500 text-white rounded text-sm" data-event-id="${event.id}">Delete</button>
                </div>
            </div>
        `;

        eventEl.querySelector('.edit-event-btn').addEventListener('click', () => editEvent(event));
        eventEl.querySelector('.delete-event-btn').addEventListener('click', () => deleteEvent(event.id));

        eventsContainer.appendChild(eventEl);
    });
}

function showEventForm() {
    const formContainer = document.getElementById('event-form-container');
    if (formContainer) {
        formContainer.classList.remove('hidden');
        editingEventId = null;
        document.getElementById('event-form').reset();
        document.getElementById('form-title').textContent = 'Create New Event';
    }
}

function hideEventForm() {
    const formContainer = document.getElementById('event-form-container');
    if (formContainer) {
        formContainer.classList.add('hidden');
        editingEventId = null;
        document.getElementById('event-form').reset();
    }
}

async function handleEventSubmit(e) {
    e.preventDefault();

    const title = document.getElementById('event-title').value.trim();
    const description = document.getElementById('event-description').value.trim();
    const dateTime = document.getElementById('event-datetime').value;
    const location = document.getElementById('event-location').value.trim();
    const groupId = document.getElementById('event-group-select').value;

    if (!title || !description || !dateTime || !groupId) {
        showError('Please fill in all required fields');
        return;
    }

    const eventData = {
        title,
        description,
        dateTime,
        location,
        groupId: parseInt(groupId)
    };

    try {
        if (editingEventId) {
            await eventAPI.updateEvent(editingEventId, eventData);
            showSuccess('Event updated successfully!');
        } else {
            await eventAPI.createEvent(eventData);
            showSuccess('Event created successfully!');
        }

        hideEventForm();
        loadUserEvents();
    } catch (error) {
        console.error('Failed to save event:', error);
        showError('Failed to save event');
    }
}

function editEvent(event) {
    editingEventId = event.id;
    
    document.getElementById('form-title').textContent = 'Edit Event';
    document.getElementById('event-title').value = event.title;
    document.getElementById('event-description').value = event.description;
    document.getElementById('event-datetime').value = formatDateTimeForInput(event.dateTime);
    document.getElementById('event-location').value = event.location || '';
    document.getElementById('event-group-select').value = event.groupId;

    const formContainer = document.getElementById('event-form-container');
    if (formContainer) {
        formContainer.classList.remove('hidden');
    }
}

async function deleteEvent(eventId) {
    if (!confirm('Are you sure you want to delete this event?')) {
        return;
    }

    try {
        await eventAPI.deleteEvent(eventId);
        showSuccess('Event deleted successfully!');
        loadUserEvents();
    } catch (error) {
        console.error('Failed to delete event:', error);
        showError('Failed to delete event');
    }
}

function filterEventsByGroup(groupId) {
    const eventCards = document.querySelectorAll('.event-card');
    
    eventCards.forEach(card => {
        if (!groupId || card.dataset.groupId === groupId) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

function formatDateTime(dateTime) {
    const date = new Date(dateTime);
    return date.toLocaleString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function formatDateTimeForInput(dateTime) {
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}