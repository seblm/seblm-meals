interface Titles {
	short: string;
	long: string;
}

export interface WeekLink {
	year: number;
	week: number;
	isActive: boolean;
}

export interface Meal {
	id: string;
	time: string;
	meal: string;
	url?: string;
}

export interface MealDay {
	reference: string;
	lunch?: Meal;
	dinner?: Meal;
}

export interface WeekMeals {
	titles: Titles;
	previous: WeekLink;
	now: WeekLink;
	next: WeekLink;
	monday: MealDay;
	tuesday: MealDay;
	wednesday: MealDay;
	thursday: MealDay;
	friday: MealDay;
	saturday: MealDay;
	sunday: MealDay;
}

const emptyTitles: Titles = {
	short: '',
	long: ''
};

const emptyWeekLink: WeekLink = {
	year: 0,
	week: 0,
	isActive: false
};

const emptyDay: MealDay = {
	reference: new Date().toDateString()
};

export const emptyWeekMeals: WeekMeals = {
	titles: emptyTitles,
	previous: emptyWeekLink,
	now: emptyWeekLink,
	next: emptyWeekLink,
	monday: emptyDay,
	tuesday: emptyDay,
	wednesday: emptyDay,
	thursday: emptyDay,
	friday: emptyDay,
	saturday: emptyDay,
	sunday: emptyDay
};

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
