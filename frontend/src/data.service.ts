const http = 'https://';
const ws = 'ws://';
const serverHost = 'chess-server-gvgy.onrender.com:8080';
const frontHost = 'chess-frontend-wpc3.onrender.com:4200';
const appPrefix = '/chess';

https://chess-frontend-wpc3.onrender.com

export const restUrl = http + serverHost + appPrefix
export const wsConnect = ws + serverHost + appPrefix + '/connect'

export const restRooms = restUrl + '/rooms'

export const wsRooms = appPrefix + '/rooms'

export const roomsLink = http + frontHost + appPrefix + '/rooms'

export const restGamePlay = restUrl + '/games';

export const wsGamePlay = appPrefix + '/games';


