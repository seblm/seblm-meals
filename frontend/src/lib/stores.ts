import { CalendarMode } from '$lib/utils/enums';
import { writable } from 'svelte/store';

export const date = writable(new Date());
export const calendarViewMode = writable<CalendarMode>(CalendarMode.WEEK);
