import type { Delai } from "./type";

export interface FiltersInterface {
    search: string;
    nbCartesRange: [number, number];
    delai: Delai;
}

export interface FilterUpdate {
    search?: string;
    nbCartesRange?: [number, number];
    delai?: Delai;
}