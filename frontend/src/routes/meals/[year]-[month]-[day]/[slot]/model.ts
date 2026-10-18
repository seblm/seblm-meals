import type { SuggestResponse } from '#lib/model/WeekMeals.ts';

export interface Model {
	suggestResponse: SuggestResponse;
	day: string;
	isLunch: boolean;
}
