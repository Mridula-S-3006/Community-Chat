const API_BASE_URL = 'http://localhost:8080/api';

function getToken() {
    return sessionStorage.getItem('jwt_token');
}

function setToken(token) {
    sessionStorage.setItem('jwt_token', token);
}

function removeToken() {
    sessionStorage.removeItem('jwt_token');
}

function getCurrentUser() {
    const userStr = sessionStorage.getItem('current_user');
    return userStr ? JSON.parse(userStr) : null;
}

function setCurrentUser(user) {
    sessionStorage.setItem('current_user', JSON.stringify(user));
}

function removeCurrentUser() {
    sessionStorage.removeItem('current_user');
}

async function apiCall(endpoint, options = {}) {
    const token = getToken();
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        
        if (response.status === 401) {
            removeToken();
            removeCurrentUser();
            window.location.href = '/login.html';
            throw new Error('Session expired. Please login again.');
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        
        return null;
    } catch (error) {
        console.error('API call failed:', error);
        throw error;
    }
}

const authAPI = {
    login: (credentials) => apiCall('/auth/login', {
        method: 'POST',
        body: JSON.stringify(credentials)
    }),
    
    signup: (userData) => apiCall('/auth/signup', {
        method: 'POST',
        body: JSON.stringify(userData)
    }),
    
    logout: () => {
        removeToken();
        removeCurrentUser();
        window.location.href = '/login.html';
    }
};

const userAPI = {
    getCurrentUser: () => apiCall('/users/me'),
    searchUsers: (query) => apiCall(`/users/search?query=${encodeURIComponent(query)}`),
    getUser: (userId) => apiCall(`/users/${userId}`)
};

const messageAPI = {
    getConversations: () => apiCall('/chat/conversations'),
    getMessages: (userId) => apiCall(`/chat/messages/${userId}`),
    sendMessage: (messageData) => apiCall('/chat/send', {
        method: 'POST',
        body: JSON.stringify(messageData)
    })
};

const groupAPI = {
    getAllGroups: () => apiCall('/groups'),
    getGroup: (groupId) => apiCall(`/groups/${groupId}`),
    createGroup: (groupData) => apiCall('/groups', {
        method: 'POST',
        body: JSON.stringify(groupData)
    }),
    updateGroup: (groupId, groupData) => apiCall(`/groups/${groupId}`, {
        method: 'PUT',
        body: JSON.stringify(groupData)
    }),
    deleteGroup: (groupId) => apiCall(`/groups/${groupId}`, {
        method: 'DELETE'
    }),
    addMember: (groupId, userId) => apiCall(`/groups/${groupId}/members`, {
        method: 'POST',
        body: JSON.stringify({ userId })
    }),
    removeMember: (groupId, userId) => apiCall(`/groups/${groupId}/members/${userId}`, {
        method: 'DELETE'
    }),
    getMessages: (groupId) => apiCall(`/groups/${groupId}/messages`)
};

const eventAPI = {
    getUserEvents: () => apiCall('/events/user'),
    getEvent: (eventId) => apiCall(`/events/${eventId}`),
    createEvent: (eventData) => apiCall('/events', {
        method: 'POST',
        body: JSON.stringify(eventData)
    }),
    updateEvent: (eventId, eventData) => apiCall(`/events/${eventId}`, {
        method: 'PUT',
        body: JSON.stringify(eventData)
    }),
    deleteEvent: (eventId) => apiCall(`/events/${eventId}`, {
        method: 'DELETE'
    }),
    rsvpEvent: (eventId, status) => apiCall(`/events/${eventId}/rsvp`, {
        method: 'POST',
        body: JSON.stringify({ status })
    }),
    getEventRsvps: (eventId) => apiCall(`/events/${eventId}/rsvps`)
};

const calendarAPI = {
    getCalendarEvents: () => apiCall('/calendar/events')
};

function isAuthenticated() {
    return getToken() !== null;
}

function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

function showError(message, elementId = 'error-message') {
    const errorEl = document.getElementById(elementId);
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.style.display = 'block';
        setTimeout(() => {
            errorEl.style.display = 'none';
        }, 5000);
    } else {
        alert(message);
    }
}

function showSuccess(message, elementId = 'success-message') {
    const successEl = document.getElementById(elementId);
    if (successEl) {
        successEl.textContent = message;
        successEl.style.display = 'block';
        setTimeout(() => {
            successEl.style.display = 'none';
        }, 3000);
    }
}