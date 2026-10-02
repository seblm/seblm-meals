import { type Writable, writable } from 'svelte/store';

export const date: Writable<Date> = writable(new Date());
