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
	image?: string;
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

export interface WeekMealsCenteredAroundADay {
	days: Array<Day>;
}

export interface MealSuggest {
	count: number;
	descriptionLabel: string;
	lastUsed: number;
	meal: Meal;
}

export interface SuggestResponse {
	mostRecents: MealSuggest[];
	fourWeeksAgo?: MealSuggest;
	fiftyTwoWeeksAgo?: MealSuggest;
}

export interface LinkOrInsert {
	mealDescription: string;
	mealTime: string;
}

export interface UnlinkMeal {
	mealTime: string;
}
