
const socket = new WebSocket("ws://localhost:8080");

socket.addEventListener("open", () => {
  console.log("Connected to server");
});

socket.addEventListener("message", (event) => {
  console.log("Message:", event.data);
});

socket.addEventListener("close", () => {
  console.log("Disconnected from server");
});

socket.addEventListener("error", (error) => {
  console.error("WebSocket error:", error);
});
export function sendMessage(message: string) {
  if (socket.readyState === WebSocket.OPEN) {
    socket.send(message);
  } else {
    console.error("WebSocket is not open. Ready state:", socket.readyState);
  }
}
export { socket };
