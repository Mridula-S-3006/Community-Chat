// chat.js

document.addEventListener("DOMContentLoaded", () => {
  const inputField = document.querySelector("input[placeholder='Type a message...']");
  const sendButton = document.querySelector("button");
  const chatContainer = document.querySelector("main > div.flex-1");

  const appendMessage = (message, sender = "self") => {
    const msgDiv = document.createElement("div");
    msgDiv.textContent = message;
    msgDiv.className = sender === "self"
      ? "text-right my-2 text-background-dark"
      : "text-left my-2 text-background-dark/80";
    chatContainer.appendChild(msgDiv);
    chatContainer.scrollTop = chatContainer.scrollHeight;
  };

  sendButton.addEventListener("click", () => {
    const msg = inputField.value.trim();
    if (!msg) return;
    appendMessage(msg, "self");

    // TODO: send to backend via WebSocket/REST
    inputField.value = "";
  });

  inputField.addEventListener("keypress", (e) => {
    if (e.key === "Enter") sendButton.click();
  });

  // TODO: handle receiving messages from backend and call appendMessage(msg, "other")
});
