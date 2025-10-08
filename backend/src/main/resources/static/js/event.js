document.addEventListener("DOMContentLoaded", () => {
  const createEventBtn = document.querySelector("button.bg-primary");
  const exploreEventsBtn = document.querySelectorAll("button.bg-primary")[1];

  createEventBtn.addEventListener("click", () => {
    alert("Redirect to Create Event page or open modal (to be implemented).");
    // TODO: integrate backend create-event functionality
  });

  exploreEventsBtn.addEventListener("click", () => {
    alert("Redirect to Explore Events page (to be implemented).");
    // TODO: integrate backend list of events
  });
});
