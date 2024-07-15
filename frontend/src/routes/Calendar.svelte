<script lang="ts">
	import { date } from '$lib/stores';
	import { onDestroy } from 'svelte';
	import { getRangeOfMeals } from '$lib/utils/api';
	import { addDays, format } from 'date-fns';
	import DayCard from './DayCard.svelte';

	$: visibleDays = getVisibleDays(selectedDate, 14)
	let selectedDate: Date;

	const dateUnsubscribe = date.subscribe((d) => {
		selectedDate = d;
		getRangeOfMeals(format(selectedDate, 'yyyy-MM-dd')).then(response => console.log(response))
	});

	const getVisibleDays = (selectedDay: Date, count: number = 7) => {
		const days: Date[] = [];
		for (let i = 0; i < count; i++) {
			days.push(addDays(selectedDate, i));
		}
		console.log(days);
		return days;
	}

	onDestroy(() => {
		dateUnsubscribe();
	});
</script>

<div class="calendar">
	<main class="calendar-content">
		{#each visibleDays as day}
			<DayCard day={day}/>
		{/each}
	</main>
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
