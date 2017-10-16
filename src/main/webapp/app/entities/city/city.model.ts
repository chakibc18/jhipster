import { BaseEntity, User } from './../../shared';

export class City implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public latitude?: string,
        public longitude?: string,
        public users?: User[],
    ) {
    }
}
