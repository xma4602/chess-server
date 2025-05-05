import {restUsers} from '../data.service';

export class User {
  constructor(
    public id: string,
    public login: string,
    public rating: number,
    public roles: string[],
    public password: string | null = null) {
  }

  getAvatarLink() {
    return `${restUsers}/${this.id}/avatar`
  }
}
