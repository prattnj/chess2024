//common functionality
let authToken = '';
let gameID = 0;

function scrollToId(id) {
  window.scrollBy({
    top: document.getElementById(id).getBoundingClientRect().top,
    behavior:"smooth"
  });
}


//HTTP
function submit() {
  document.getElementById('response').value = '';
  const method = document.getElementById('method').value;
  const endpoint = document.getElementById('handleBox').value;
  const requestBody = document.getElementById('requestBox').value;
  authToken = document.getElementById('authToken').value;

  if (endpoint && method) {
    send(endpoint, requestBody, method);
  }

  try {
    const requestObj = JSON.parse(requestBody);
    if(requestObj.gameID) {
      gameID = requestObj.gameID;
    }
  } catch (ignored) {}

  return false;
}

function send(path, params, method) {
  params = !!params ? params : undefined;
  let status = '';
  fetch(path, {
    method: method,
    body: params,
    headers: {
      Authorization: authToken,
      'Content-Type': 'application/json',
    },
  })
    .then((response) => {
      status = response.status + ': ' + response.statusText + '\n';
      return response.text();
    })
    .then((text) => {
      if(text) return JSON.parse(text);
      else return text;
    })
    .then((data) => {
      if(data) {
        if(data.authToken) {
          authToken = data.authToken;
          document.getElementById('authToken').value = authToken;
        }
        if(data.gameID) {
          gameID = data.gameID;
        }
      }
      const response = (data === "") ? "Empty response body" : JSON.stringify(data, null, 2);
      document.getElementById('response').innerText = status + "\n" + response;
      scrollToId('responseBox');
    })
    .catch((error) => {
      document.getElementById('response').innerText = error;
    });
}

function displayRequest(method, endpoint, request) {
  document.getElementById('method').value = method;
  document.getElementById('handleBox').value = endpoint;
  const body = request ? JSON.stringify(request, null, 2) : '';
  document.getElementById('requestBox').value = body;
  scrollToId('execute');
}

function clearAll() {
  displayRequest('DELETE', '/db', null);
}
function register() {
  displayRequest('POST', '/user', { username: 'username', password: 'password', email: 'email' });
}
function login() {
  displayRequest('POST', '/session', { username: 'username', password: 'password' });
}
function logout() {
  displayRequest('DELETE', '/session', null);
}
function gamesList() {
  displayRequest('GET', '/game', null);
}
function createGame() {
  displayRequest('POST', '/game', { gameName: 'gameName' });
}
function joinGame() {
  displayRequest('PUT', '/game', { playerColor: 'WHITE/BLACK', gameID: gameID });
}
//End HTTP


//Websocket
let socket;
let lastMove = {
  startPosition: {
    row: 1,
    column: 1,
  },
  endPosition: {
    row: 1,
    column: 1,
  },
  promotionPiece: null,
};
let messages = 0;

function socketOpen(event) {
  displayMessage('Connected to Websocket');
}

function socketMessage(event) {
  try {
    displayMessage(JSON.stringify(JSON.parse(event.data), null, 2));
  } catch (e) {
    displayMessage(e)
  }
}

function socketError(event) {
  displayMessage("Error: " + event.reason)
}

function socketClose(event) {
  document.getElementById('websocketConnectBox').style.removeProperty("display");
  document.getElementById('executeWebsocket').style.display = "none";
  const messageBox = document.getElementById("wsMessageBox");
  while(messageBox.childElementCount > 1) messageBox.removeChild(messageBox.lastChild);
}


function connectWs() {
  socket = new WebSocket(`ws://${window.location.host}/ws`);
  socket.addEventListener("open", socketOpen);
  socket.addEventListener("message", socketMessage);
  socket.addEventListener("error", socketError);
  socket.addEventListener("close", socketClose);

  document.getElementById('websocketConnectBox').style.display = "none";
  document.getElementById('executeWebsocket').style.removeProperty("display");
}

function sendWs() {
  const data = document.getElementById('commandBox').value;
  socket.send(data);

  try {
    const dataObj = JSON.parse(data);
    gameID = data.gameID || gameID;
    lastMove = data.move || lastMove;
  } catch (ignored) {}
}

function displayCommand(commandType, extra) {
  const command = {commandType: commandType, authToken: authToken, gameID: gameID, ...extra}
  const json = JSON.stringify(command, null, 2);
  document.getElementById('commandBox').value = json;
  scrollToId('sendWs');
}

function displayMessage(message) {
  const messageBox = document.getElementById("wsMessageBox");
  if(messageBox.childElementCount > 1) messageBox.appendChild(document.createElement("br"));
  messages++;
  const preElement = document.createElement("pre");
  preElement.id = "message" + messages
  preElement.innerText = new Date().toLocaleTimeString() + "\n" + message;
  messageBox.appendChild(preElement);
  scrollToId(preElement.id);
}


function connect() {
  displayCommand("CONNECT")
}
function makeMove() {
  displayCommand("MAKE_MOVE", {move: lastMove})
}
function resign() {
  displayCommand("RESIGN")
}
function leave() {
  displayCommand("LEAVE")
}
