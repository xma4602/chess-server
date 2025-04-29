const http = 'https://';
const ws = 'wss://';
const serverHost = 'chess-server-gvgy.onrender.com';
const frontHost = 'chess-frontend-wpc3.onrender.com';
const restPrefix = '/chess';
const topicPrefix = '/topic';
export const restUrl = http + serverHost + restPrefix
export const restRooms = restUrl + '/rooms'
export const restRoomsLink = http + frontHost + restPrefix + '/rooms'
export const restGamePlay = restUrl + '/games';
export const restChat = restUrl + '/chats';

export const wsConnect = ws + serverHost + '/ws'
export const wsRooms = topicPrefix + '/rooms'
export const wsGamePlay = topicPrefix + '/games';

export const wsChat = topicPrefix + '/chats';
