
export class User {
  constructor(
    public id: string,
    public login: string,
    public rating: number,
    public roles: string[]) {
  }
}
