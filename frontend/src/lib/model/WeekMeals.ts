interface Titles {
	short: string;
	long: string;
}

export interface WeekLink {
	year: number;
	week: number;
	isActive: boolean;
}

export interface MealEntry {
	meal: Meal;
	time: string;
}

export interface Meal {
	id: string;
	description: string;
	url?: string;
}

export interface MealStatistics {
	count: number;
	first: Date;
	last: Date;
	meal: Meal;
}

export interface Day {
	reference: string;
	lunch?: MealEntry;
	dinner?: MealEntry;
}

export interface WeekMeals {
	titles: Titles;
	previous: WeekLink;
	now: WeekLink;
	next: WeekLink;
	monday: Day;
	tuesday: Day;
	wednesday: Day;
	thursday: Day;
	friday: Day;
	saturday: Day;
	sunday: Day;
}

export interface SearchSuggestion {
	count: number;
	description: string;
	descriptionLabel: string;
	lastused: number;
}

export interface SuggestionResponse {
	mostRecents: SearchSuggestion[];
	fourWeeksAgo?: SearchSuggestion;
	fiftyTwoWeeksAgo?: SearchSuggestion;
}

export interface LinkOrInsert {
	mealDescription: string;
	mealTime: string;
}

export interface UnlinkMeal {
	mealTime: string;
}
