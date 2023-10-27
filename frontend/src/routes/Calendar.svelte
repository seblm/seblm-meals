<script lang="ts">
	import { calendarViewMode, date } from '$lib/stores';
	import { CalendarMode } from '$lib/utils/enums';
	import { onDestroy } from 'svelte';
	import MonthCalendar from './MonthCalendar.svelte';
	import WeekCalendar from './WeekCalendar.svelte';

	let mode: CalendarMode = CalendarMode.MONTH;
	let selectedDate: Date;

	const dateUnsubscribe = date.subscribe((d) => {
		selectedDate = d;
	});

	const calendarModeUnsubscribe = calendarViewMode.subscribe((calendarMode) => {
		mode = calendarMode;
	});

	const toggleCalendarMode = () =>
		calendarViewMode.update((current) =>
			current === CalendarMode.WEEK ? CalendarMode.MONTH : CalendarMode.WEEK
		);

	onDestroy(() => {
		dateUnsubscribe();
		calendarModeUnsubscribe();
	});
</script>

<div class="calendar">
	<header class="calendar-header">
		<h1>Votre {mode === CalendarMode.MONTH ? 'mois' : 'semaine'}</h1>
		<button on:click={toggleCalendarMode}
			>Aller à la vue {mode === CalendarMode.MONTH ? 'semaine' : 'mois'}</button
		>
	</header>
	<main class="calendar-content">
		{#if mode === CalendarMode.WEEK}
			<WeekCalendar />
		{:else}
			<MonthCalendar />
		{/if}
	</main>
</div>

<style lang="scss">
	@import '../lib/styles/variables';

	.calendar {
		width: 100%;
		&-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin: 24px 0;
			h1 {
				grid-column: 2 / span 1;
				margin: 0;
			}
			button {
				grid-column: 3 / span 1;
				display: block;
				background: transparent;
				color: var(--color-theme-1);
				border: none;
				cursor: pointer;
				transition: 0.2s ease-in;
				font-size: 1rem;
				font-weight: 700;
				&:focus,
				&:hover {
					filter: brightness(0.7);
				}
			}
		}
	}

	@media (min-width: $screen-m) {
		.calendar {
			&-header {
				display: grid;
				grid-template-columns: repeat(3, 1fr);

				button {
					font-size: 1.2rem;
				}
			}
		}
	}
</style>
