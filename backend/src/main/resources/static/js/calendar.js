// calendar.js

const calendarContainer = document.querySelector(".grid.grid-cols-7");
const monthLabel = document.querySelector("h3.text-lg.font-bold");
const prevBtn = document.querySelector("button span:contains('chevron_left')")?.parentElement;
const nextBtn = document.querySelector("button span:contains('chevron_right')")?.parentElement;

// Example event data (replace with API call later)
const events = {
  "2024-10-5": ["Meeting", "Workshop"],
  "2024-10-12": ["Birthday"],
  "2024-10-20": ["Conference"]
};

let currentDate = new Date();

function renderCalendar(date) {
  const year = date.getFullYear();
  const month = date.getMonth();

  const monthNames = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  ];
  monthLabel.textContent = `${monthNames[month]} ${year}`;

  const days = Array.from(calendarContainer.children).slice(7);
  days.forEach(d => d.remove());

  const firstDay = new Date(year, month, 1).getDay();
  const lastDay = new Date(year, month + 1, 0).getDate();


  for (let i = 0; i < firstDay; i++) {
    const empty = document.createElement("div");
    calendarContainer.appendChild(empty);
  }

  for (let day = 1; day <= lastDay; day++) {
    const dayBtn = document.createElement("div");
    dayBtn.className = "flex flex-col items-center justify-center p-1";

    const btn = document.createElement("button");
    btn.className = "flex h-10 w-10 items-center justify-center rounded-full text-sm font-medium text-slate-700 hover:bg-primary/20";
    btn.textContent = day;

    const today = new Date();
    if (day === today.getDate() && month === today.getMonth() && year === today.getFullYear()) {
      btn.className = "flex h-10 w-10 items-center justify-center rounded-full bg-primary text-sm font-bold text-black";
    }

    dayBtn.appendChild(btn);

    const dateKey = `${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
    if (events[dateKey]) {
      const dotsContainer = document.createElement("div");
      dotsContainer.className = "mt-1 flex space-x-1";

      events[dateKey].forEach(() => {
        const dot = document.createElement("span");
        dot.className = "h-1.5 w-1.5 rounded-full bg-primary";
        dotsContainer.appendChild(dot);
      });

      dayBtn.appendChild(dotsContainer);
    }

    calendarContainer.appendChild(dayBtn);
  }
}

prevBtn?.addEventListener("click", () => {
  currentDate.setMonth(currentDate.getMonth() - 1);
  renderCalendar(currentDate);
});
nextBtn?.addEventListener("click", () => {
  currentDate.setMonth(currentDate.getMonth() + 1);
  renderCalendar(currentDate);
});

renderCalendar(currentDate);
