const http = 'http://';
const ws = 'ws://';
const serverHost = 'localhost:8080';
const frontHost = 'localhost:4200';
const appPrefix = '/chess';
export const restUrl = http + serverHost + appPrefix

export const wsConnect = ws + serverHost + appPrefix + '/connect'

export const restRooms = restUrl + '/rooms'

export const wsRooms = appPrefix + '/rooms'

export const roomsLink = http + frontHost + appPrefix + '/rooms'

export const restGamePlay = restUrl + '/games';

export const wsGamePlay = appPrefix + '/games';


