const http = 'http://';
const host = 'localhost:8080';
const appPrefix = '/chess';
export const restUrl = http + host + appPrefix

export const wsUrl = http + host + appPrefix + '/connect'

export const roomsUrl = restUrl + '/rooms'

export const gameplayUrl = restUrl + '/games';
