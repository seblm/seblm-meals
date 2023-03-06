import { emptyWeekMeals } from '$lib/model/WeekMeals';
import type WeekMeals from '$lib/model/WeekMeals';
import { CalendarMode } from '$lib/utils/enums';
import { writable } from 'svelte/store';

export const date = writable(new Date());
export const calendarViewMode = writable<CalendarMode>(CalendarMode.WEEK);

export const selectedWeekMeals = writable<WeekMeals>(emptyWeekMeals);
