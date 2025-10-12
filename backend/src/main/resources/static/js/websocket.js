let stompClient = null;
let isConnected = false;
let subscriptions = {};

function connectWebSocket() {
    const socket = new SockJS('http://localhost:8080/ws');
    stompClient = Stomp.over(socket);

    const token = getToken();
    const headers = token ? { 'Authorization': `Bearer ${token}` } : {};

    stompClient.connect(headers, onConnected, onError);
}

function onConnected() {
    isConnected = true;
    console.log('WebSocket connected');
    
    if (window.onWebSocketConnected) {
        window.onWebSocketConnected();
    }
}

function onError(error) {
    isConnected = false;
    console.error('WebSocket connection error:', error);
    
    setTimeout(() => {
        console.log('Attempting to reconnect...');
        connectWebSocket();
    }, 5000);
}

function subscribeToChat(userId, callback) {
    if (!isConnected || !stompClient) {
        console.error('WebSocket not connected');
        return null;
    }

    const destination = `/user/${userId}/queue/messages`;
    const subscription = stompClient.subscribe(destination, (message) => {
        const messageData = JSON.parse(message.body);
        callback(messageData);
    });

    subscriptions[`chat-${userId}`] = subscription;
    return subscription;
}

function subscribeToGroup(groupId, callback) {
    if (!isConnected || !stompClient) {
        console.error('WebSocket not connected');
        return null;
    }

    const destination = `/topic/group/${groupId}`;
    const subscription = stompClient.subscribe(destination, (message) => {
        const messageData = JSON.parse(message.body);
        callback(messageData);
    });

    subscriptions[`group-${groupId}`] = subscription;
    return subscription;
}

function sendChatMessage(receiverId, content) {
    if (!isConnected || !stompClient) {
        console.error('WebSocket not connected');
        return;
    }

    const currentUser = getCurrentUser();
    const message = {
        senderId: currentUser.id,
        receiverId: receiverId,
        content: content,
        timestamp: new Date().toISOString()
    };

    stompClient.send('/app/chat', {}, JSON.stringify(message));
}

function sendGroupMessage(groupId, content) {
    if (!isConnected || !stompClient) {
        console.error('WebSocket not connected');
        return;
    }

    const currentUser = getCurrentUser();
    const message = {
        senderId: currentUser.id,
        groupId: groupId,
        content: content,
        timestamp: new Date().toISOString()
    };

    stompClient.send(`/app/group/${groupId}`, {}, JSON.stringify(message));
}

function unsubscribe(key) {
    if (subscriptions[key]) {
        subscriptions[key].unsubscribe();
        delete subscriptions[key];
    }
}

function unsubscribeAll() {
    Object.keys(subscriptions).forEach(key => {
        subscriptions[key].unsubscribe();
    });
    subscriptions = {};
}

function disconnectWebSocket() {
    if (stompClient && isConnected) {
        unsubscribeAll();
        stompClient.disconnect(() => {
            isConnected = false;
            console.log('WebSocket disconnected');
        });
    }
}

function getConnectionStatus() {
    return isConnected;
}