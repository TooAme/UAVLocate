export interface IStatics {
  id?: number;
  time?: Date | null;
  posX?: number | null;
  posY?: number | null;
  posZ?: number | null;
  windSpeed?: number | null;
  windDirection?: string | null;
}

export class Statics implements IStatics {
  constructor(
    public id?: number,
    public time?: Date | null,
    public posX?: number | null,
    public posY?: number | null,
    public posZ?: number | null,
    public windSpeed?: number | null,
    public windDirection?: string | null,
  ) {}
}
