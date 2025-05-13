export class User {
  constructor(
    public id: string,
    public login: string,
    public password: string,
    public rating: number,
    public roles: string[],
    public avatarUrl: string | null = null) {
  }

  clone(): User {
    return new User(
      this.id,
      this.login,
      this.password,
      this.rating,
      [...this.roles]
    );
  }

  static fromObject(obj: {
    id: string,
    login: string,
    rating: number,
    roles: string[]
  }): User {
    return new User(
      obj.id,
      obj.login,
      '',
      obj.rating,
      obj.roles
    );
  }
}
