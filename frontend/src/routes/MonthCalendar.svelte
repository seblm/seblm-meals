<script lang="ts">
	import { calendarViewMode, date } from '$lib/stores';
	import { dayNames, monthNames } from '$lib/utils/constants';
	import { CalendarMode } from '$lib/utils/enums';
	import { getWeeksInMonth, lastDayOfMonth } from 'date-fns';

	import addMonths from 'date-fns/addMonths';
	import { onDestroy } from 'svelte';
	import { blur } from 'svelte/transition';
	import CalendarNavigation from './CalendarNavigation.svelte';

	let selectedDate: Date;
	let firstDay: Date;

	$: lastDay = lastDayOfMonth(selectedDate);
	$: monthName = monthNames[selectedDate.getMonth()];
	$: year = selectedDate.getFullYear();
	$: firstDayOfMonthInWeek = firstDay.getDay();
	$: lastDayOfMonthInWeek = lastDay.getDay();

	const unsubscribe = date.subscribe((d) => {
		selectedDate = d;
		firstDay = new Date(selectedDate);
		firstDay.setDate(1);
	});

	const onNextMonth = () => {
		const newDate = addMonths(selectedDate, 1);
		date.update(() => newDate);
	};

	const onPrevMonth = () => {
		const newDate = addMonths(selectedDate, -1);
		date.update(() => newDate);
	};

	const getDayOfMonth = (weekDay: number, week: number) =>
		week * 7 + weekDay - firstDayOfMonthInWeek + 1;

	const selectDay = (day) => {
		const newDate = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), day);
		date.update(() => newDate);
		calendarViewMode.update(() => CalendarMode.WEEK);
	}

	$: isInMonth = (weekDay: number, week: number): boolean =>
		!(firstDayOfMonthInWeek > weekDay && week === 0) &&
		!(getDayOfMonth(weekDay, week) > lastDay.getDate());

	onDestroy(unsubscribe);
</script>

<div class="month-calendar" in:blur={{ delay: 200 }} out:blur={{ duration: 200 }}>
	<CalendarNavigation
		title={`${monthName} ${year}`}
		on:previous={onPrevMonth}
		on:next={onNextMonth}
	/>
	<main class="month-calendar-content">
		<div class="month-calendar-grid">
			<div class="month-calendar-grid-row-header">
				{#each Array(7)
					.fill(null)
					.map((_, index) => index) as weekDay}
					<div class="month-calendar-grid-cell">
						{dayNames[weekDay]}
					</div>
				{/each}
			</div>
			{#each Array(getWeeksInMonth(selectedDate))
				.fill(null)
				.map((_, index) => index) as week}
				<div class="month-calendar-grid-row">
					{#each Array(7)
						.fill(null)
						.map((_, index) => index) as weekDay}
						<div
							class="month-calendar-grid-cell"
							disabled={!isInMonth(weekDay, week)}
							class:not-in-month={!isInMonth(weekDay, week)}
							on:click={() => selectDay(getDayOfMonth(weekDay, week))}
						>
							{#if isInMonth(weekDay, week)}
								<div class="day-number">
									{getDayOfMonth(weekDay, week)}
								</div>
							{/if}
							<div class="month-calendar-grid-cell-meal" />
						</div>
					{/each}
				</div>
			{/each}
		</div>
	</main>
</div>

<style lang="scss">
	.month-calendar {
		&-grid {
			background: #fff;
			&-row {
				width: 100%;
				height: 120px;
				display: flex;
				border: 1px solid var(--color-theme-1);
				border-top: none;
				&-header {
					width: 100%;
					display: flex;
					background: var(--color-theme-1);
					color: #fff;
					height: 50px;
					border: 1px solid var(--color-theme-1);
				}
			}
			&-cell {
				flex: 1;
				display: flex;
				justify-content: center;
				align-items: center;
				flex-direction: column;
				position: relative;
				cursor: pointer;
				&-meal {
					flex: 1;
					width: 100%;
				}
				.day-number {
					text-align: center;
					font-size: 0.7rem;
					background: var(--color-theme-1);
					color: #fff;
					padding: 0.5rem;
					position: absolute;
					left: 0;
					top: 0;
					border-radius: 0 0 5px 0;
				}
				&:not(:last-child) {
					border-right: 1px solid var(--color-theme-1);
				}
			}
		}
	}
	.not-in-month {
		background: var(--color-bg-1);
		filter: grayscale(1);
		pointer-events: none;
		cursor: none;
	}
</style>
