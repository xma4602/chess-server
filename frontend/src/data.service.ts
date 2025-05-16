const http = 'http://';
const ws = 'ws://';
const serverHost = '194.87.102.76:8080';
const frontHost = '194.87.102.76';
const restPrefix = '/chess';
const topicPrefix = '/topic';
export const restUrl = http + serverHost + restPrefix
export const restRooms = restUrl + '/rooms'
export const restUsers = restUrl + '/users'
export const restRoomsLink = http + frontHost + restPrefix + '/rooms'
export const restGamePlay = restUrl + '/games';
export const restChat = restUrl + '/chats';
export const restHistory = restUrl + '/history';

export const wsConnect = ws + serverHost + '/ws'
export const wsRooms = topicPrefix + '/rooms'
export const wsGamePlay = topicPrefix + '/games';

export const wsChat = topicPrefix + '/chats';
