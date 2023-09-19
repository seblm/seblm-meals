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
}

export interface Day {
	reference: string;
	lunch?: Meal;
	dinner?: Meal;
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

const emptyTitles: Titles = {
	short: '',
	long: ''
};

const emptyWeekLink: WeekLink = {
	year: 0,
	week: 0,
	isActive: false
};

const emptyDay: Day = {
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

export interface SeachSuggestions {
	count: number;
	description: string;
	descriptionLabel: string;
	lastused: number;
}

export interface LinkOrInsert {
	mealDescription: string;
	mealTime: string;
}

export interface UnlinkMeal {
	mealTime: string;
}
