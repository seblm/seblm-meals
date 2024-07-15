<script lang="ts">
	import { date } from '$lib/stores';
	import { onDestroy } from 'svelte';
	import { getRangeOfMeals } from '$lib/utils/api';
	import { addDays, format } from 'date-fns';
	import DayCard from './DayCard.svelte';
	import type { MealDay } from '$lib/model/WeekMeals';
	import Spinner from '$lib/components/Spinner.svelte';

	$: visibleDays = getVisibleDays(selectedDate, 14)
	let availableMealDays: MealDay[] = [];
	let selectedDate: Date;
	let isLoading = true;

	const dateUnsubscribe = date.subscribe((d) => {
		selectedDate = d;
		isLoading = false;
		isLoading = true;
		getRangeOfMeals(format(selectedDate, 'yyyy-MM-dd')).then(response => {
			availableMealDays = [...response.meals];
		}).finally(() => isLoading = false);
	});

	const getVisibleDays = (selectedDay: Date, count: number = 7) => {
		const days: Date[] = [];
		for (let i = 0; i < count; i++) {
			days.push(addDays(selectedDate, i));
		}
		return days;
	}

	const getMealDayForDate = (date: Date): MealDay | undefined => {
		const reference = format(date, 'yyyy-MM-dd');
		return availableMealDays.find(mealDay => {
			return mealDay.reference === reference
		});
	}

	onDestroy(() => {
		dateUnsubscribe();
	});
</script>

<div class="calendar">
	{#if (isLoading)}
		<Spinner />
	{:else}
		<main class="calendar-content">
			{#each visibleDays as day}
				<DayCard day={day} meals={getMealDayForDate(day)}/>
			{/each}
		</main>
	{/if}

</div>

<style lang="scss">
	@import '../lib/styles/variables';

	.calendar {
		width: 100%;
		&-content {
			display: flex;
			flex-direction: column;
			gap: 1rem;
		}
	}
</style>
