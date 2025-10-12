let currentMonth = new Date().getMonth();
let currentYear = new Date().getFullYear();
let allEvents = [];

document.addEventListener('DOMContentLoaded', () => {
    if (!requireAuth()) return;

    loadCalendarEvents();

    const prevBtn = document.getElementById('prev-month-btn');
    const nextBtn = document.getElementById('next-month-btn');
    const todayBtn = document.getElementById('today-btn');

    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            renderCalendar();
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            renderCalendar();
        });
    }

    if (todayBtn) {
        todayBtn.addEventListener('click', () => {
            const today = new Date();
            currentMonth = today.getMonth();
            currentYear = today.getFullYear();
            renderCalendar();
        });
    }
});

async function loadCalendarEvents() {
    try {
        const events = await eventAPI.getUserEvents();
        allEvents = events;
        renderCalendar();
    } catch (error) {
        console.error('Failed to load events:', error);
        showError('Failed to load calendar events');
    }
}

function renderCalendar() {
    const calendarGrid = document.getElementById('calendar-grid');
    const monthYear = document.getElementById('month-year');

    if (!calendarGrid || !monthYear) return;

    const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'];

    monthYear.textContent = `${monthNames[currentMonth]} ${currentYear}`;

    const firstDay = new Date(currentYear, currentMonth, 1).getDay();
    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();

    calendarGrid.innerHTML = '';

    const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    dayNames.forEach(day => {
        const dayHeader = document.createElement('div');
        dayHeader.className = 'text-center font-bold py-2 text-gray-600';
        dayHeader.textContent = day;
        calendarGrid.appendChild(dayHeader);
    });

    for (let i = 0; i < firstDay; i++) {
        const emptyCell = document.createElement('div');
        emptyCell.className = 'calendar-day empty';
        calendarGrid.appendChild(emptyCell);
    }

    const today = new Date();
    const isCurrentMonth = currentMonth === today.getMonth() && currentYear === today.getFullYear();

    for (let day = 1; day <= daysInMonth; day++) {
        const dayCell = document.createElement('div');
        dayCell.className = 'calendar-day border rounded-lg p-2 min-h-24 hover:bg-gray-50 cursor-pointer';

        const isToday = isCurrentMonth && day === today.getDate();
        if (isToday) {
            dayCell.classList.add('bg-primary', 'bg-opacity-20', 'border-primary');
        }

        const dayNumber = document.createElement('div');
        dayNumber.className = `text-sm font-semibold mb-1 ${isToday ? 'text-primary' : 'text-gray-700'}`;
        dayNumber.textContent = day;
        dayCell.appendChild(dayNumber);

        const dayEvents = getEventsForDate(currentYear, currentMonth, day);
        dayEvents.forEach(event => {
            const eventDot = document.createElement('div');
            eventDot.className = 'text-xs truncate bg-primary text-black px-1 py-0.5 rounded mb-1';
            eventDot.textContent = event.title;
            eventDot.title = `${event.title} - ${event.groupName}`;
            dayCell.appendChild(eventDot);
        });

        dayCell.addEventListener('click', () => showDayEvents(day, dayEvents));

        calendarGrid.appendChild(dayCell);
    }
}

function getEventsForDate(year, month, day) {
    return allEvents.filter(event => {
        const eventDate = new Date(event.dateTime);
        return eventDate.getFullYear() === year &&
               eventDate.getMonth() === month &&
               eventDate.getDate() === day;
    });
}

function showDayEvents(day, events) {
    const modal = document.getElementById('day-events-modal');
    const modalContent = document.getElementById('modal-events-list');
    const modalTitle = document.getElementById('modal-day-title');

    if (!modal || !modalContent || !modalTitle) return;

    const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'];

    modalTitle.textContent = `${monthNames[currentMonth]} ${day}, ${currentYear}`;
    modalContent.innerHTML = '';

    if (events.length === 0) {
        modalContent.innerHTML = '<p class="text-gray-500 text-center py-8">No events on this day</p>';
    } else {
        events.forEach(event => {
            const eventEl = document.createElement('div');
            eventEl.className = 'event-item p-4 bg-white border rounded-lg mb-3';
            eventEl.innerHTML = `
                <div class="flex justify-between items-start">
                    <div class="flex-1">
                        <div class="flex items-center gap-2 mb-1">
                            <h4 class="font-bold">${escapeHtml(event.title)}</h4>
                            <span class="text-xs bg-primary text-black px-2 py-1 rounded">${escapeHtml(event.groupName)}</span>
                        </div>
                        <p class="text-sm text-gray-600">${formatTime(event.dateTime)}</p>
                        ${event.location ? `<p class="text-sm text-gray-500 mt-1">📍 ${escapeHtml(event.location)}</p>` : ''}
                        <p class="text-sm text-gray-700 mt-2">${escapeHtml(event.description)}</p>
                    </div>
                </div>
            `;
            modalContent.appendChild(eventEl);
        });
    }

    modal.classList.remove('hidden');
}

function closeDayEventsModal() {
    const modal = document.getElementById('day-events-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

document.addEventListener('click', (e) => {
    const modal = document.getElementById('day-events-modal');
    if (e.target === modal) {
        closeDayEventsModal();
    }
});

const closeModalBtn = document.getElementById('close-modal-btn');
if (closeModalBtn) {
    closeModalBtn.addEventListener('click', closeDayEventsModal);
}

function formatTime(dateTime) {
    const date = new Date(dateTime);
    return date.toLocaleTimeString('en-US', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: true
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}