document.addEventListener("DOMContentLoaded", () => {
  const createBtn = document.querySelector("aside button");
  const sendBtn = document.querySelector("footer button:last-child");
  const inputField = document.querySelector("footer input");
  const mainContainer = document.querySelector("main > div");

  createBtn.addEventListener("click", () => {
    alert("Community creation functionality will be implemented here.");
    // TODO: Open modal or redirect to create community page
  });

  sendBtn.addEventListener("click", () => {
    const msg = inputField.value.trim();
    if (!msg) return;
    const msgEl = document.createElement("div");
    msgEl.textContent = msg;
    msgEl.className = "text-left my-2 text-black"; // adjust styling as needed
    mainContainer.appendChild(msgEl);
    inputField.value = "";

    // TODO: send message to backend for this community
  });

  inputField.addEventListener("keypress", (e) => {
    if (e.key === "Enter") sendBtn.click();
  });
});
