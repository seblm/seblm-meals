const nowAsDate = new Date();

function computeWeekNumber(day: number, month: number, year: number): number {
	const januaryFirst = new Date(year, 0, 1);
	const date = new Date(year, month - 1, day);
	const numberOfDays = Math.floor(
		(date.valueOf() - januaryFirst.valueOf()) / (24 * 60 * 60 * 1000)
	);
	return Math.ceil((day + 1 + numberOfDays) / 7);
}

export const now = {
	day: nowAsDate.getDate(),
	month: nowAsDate.getMonth() + 1,
	weekNumber: computeWeekNumber(nowAsDate.getDate(), nowAsDate.getMonth(), nowAsDate.getFullYear()),
	year: nowAsDate.getFullYear()
};

export const currentDay = $state(now);

export function changeDay(day: { year: number; month: number; day: number }): void {
	currentDay.day = day.day;
	currentDay.month = day.month;
	currentDay.weekNumber = computeWeekNumber(day.day, day.month, day.year);
	currentDay.year = day.year;
}
