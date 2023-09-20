<script lang="ts">
	import bin from '$lib/images/bin';
	import { check } from '$lib/images/check';
	import type { Day, SuggestionResponse, WeekMeals } from '$lib/model/WeekMeals';
	import { date } from '$lib/stores';
	import { getSuggestions, getWeekMeals, linkOrInsert, unlink } from '$lib/utils/api';
	import { clickOutside } from '$lib/utils/ClickOutside';
	import { getDayName } from '$lib/utils/functions';
	import { getWeek, getYear } from 'date-fns';
	import { addWeeks } from 'date-fns/fp';
	import { onDestroy, onMount } from 'svelte';
	import { blur } from 'svelte/transition';
	import CalendarNavigation from './CalendarNavigation.svelte';
	import MealInput from './MealInput.svelte';
	import SnackBar from './SnackBar.svelte';

	const days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];

	interface InputValue {
		day: string;
		isLunch: boolean;
		mealDescription: string;
	}

	interface InputSuggestions {
		input: string;
		suggestions: SuggestionResponse;
	}

	let selectedDate: Date;
	let weekMeals: WeekMeals;
	let inputValues: Record<string, InputValue> = {};
	let snackBarVisible = false;
	let snackBarContent = '';
	let snackBarStatus: 'success' | 'error' = 'success';
	const suggestions: InputSuggestions = {
		input: '',
		suggestions: {
			mostRecents: []
		}
	};

	const unsubscribe = date.subscribe((d) => {
		selectedDate = d;
	});

	$: weekNumber = getWeek(selectedDate);

	onMount(() => {
		updateWeekMeals();
	});

	const updateWeekMeals = () =>
		getWeekMeals(getYear(selectedDate), getWeek(selectedDate)).then((meals) => (weekMeals = meals));
	const getMeal = (dayString: string, isLunch: boolean): string => {
		let dayMeals = getDay(dayString);

		let meal = isLunch ? dayMeals?.lunch?.meal ?? '' : dayMeals?.dinner?.meal;
		return meal ? meal : '';
	};

	const getDay = (dayString: string): Day | null => {
		let dayConfig = null;
		switch (dayString) {
			case 'monday':
				dayConfig = weekMeals.monday;
				break;
			case 'tuesday':
				dayConfig = weekMeals.tuesday;
				break;
			case 'wednesday':
				dayConfig = weekMeals.wednesday;
				break;
			case 'thursday':
				dayConfig = weekMeals.thursday;
				break;
			case 'friday':
				dayConfig = weekMeals.friday;
				break;
			case 'saturday':
				dayConfig = weekMeals.saturday;
				break;
			case 'sunday':
				dayConfig = weekMeals.sunday;
				break;
			default:
				return null;
		}

		return dayConfig;
	};

	const saveMeal = (day: string, isLunch: boolean) => {
		const mealDescription = inputValues[`${day}-${isLunch}`]?.mealDescription;
		if (!mealDescription) {
			return;
		}

		const dayDate = getDay(day)?.reference;
		if (dayDate) {
			linkOrInsert(mealDescription, dayDate!, isLunch).then(
				() => {
					showSnackBar('Repas enregistré', 'success');
					updateWeekMeals();
				},
				(error) => {
					showSnackBar("Erreur lors de l'enregistrement : " + error, 'error');
				}
			);
		}
	};

	const unlinkMeal = (day: string, isLunch: boolean) => {
		const dayDate = getDay(day)?.reference;
		if (dayDate) {
			unlink(dayDate!, isLunch).then(
				() => {
					showSnackBar('Repas supprimé', 'success');
					updateWeekMeals();
				},
				(error) => {
					showSnackBar('Erreur lors de la suppression : ' + error, 'error');
				}
			);
		}
	};

	const onInputChange = (mealDescription: string, day, isLunch) => {
		const recordKey = `${day}-${isLunch}`;
		const ref = getDay(day)!.reference;

		inputValues[recordKey] = {
			day,
			isLunch,
			mealDescription
		};
		getSuggestions(ref, mealDescription, isLunch).then((response) => {
			suggestions.input = recordKey;
			suggestions.suggestions = response;
		});
	};

	const onSelectSuggestion = (day: string, isLunch: boolean, suggestion: string) => {
		const recordKey = `${day}-${isLunch}`;

		inputValues[recordKey] = {
			...inputValues[recordKey],
			mealDescription: suggestion
		};

		saveMeal(day, isLunch);
		reinitSuggestions();
	};

	const reinitSuggestions = () => {
		suggestions.suggestions = { mostRecents: [] };
		suggestions.input = '';
	};

	const nextWeek = () => {
		const newDate = addWeeks(1, selectedDate);
		date.update(() => newDate);
		updateWeekMeals();
	};

	const prevWeek = () => {
		const newDate = addWeeks(-1, selectedDate);
		date.update(() => newDate);
		updateWeekMeals();
	};

	const showSnackBar = (message: string, status: 'success' | 'error') => {
		snackBarContent = message;
		snackBarStatus = status;
		snackBarVisible = true;
		setTimeout(() => {
			snackBarVisible = false;
		}, 3000);
	};

	const handleClickOutsideCell = (selector: string) => {
		if (selector === suggestions.input) {
			reinitSuggestions();
		}
	};

	onDestroy(unsubscribe);
</script>

{#if weekMeals}
	<div in:blur={{ delay: 200 }} out:blur={{ duration: 200 }}>
		<CalendarNavigation title={weekMeals.titles.long} on:previous={prevWeek} on:next={nextWeek} />
	</div>
{/if}
<div class="week-calendar-wrapper" in:blur={{ delay: 200 }} out:blur={{ duration: 200 }}>
	<div class="week-calendar-day week-calendar-heading-row">
		<div class="week-calendar-day-name week-calendar-day-cell week-calendar-heading-cell" />
		<div class="week-calendar-day-meal week-calendar-day-cell week-calendar-heading-cell">Midi</div>
		<div class="week-calendar-day-cell week-calendar-day-actions week-calendar-heading-cell" />
		<div class="week-calendar-day-meal week-calendar-day-cell week-calendar-heading-cell">Soir</div>
		<div class="week-calendar-day-cell week-calendar-day-actions week-calendar-heading-cell" />
	</div>
	{#each days as day}
		<div class="week-calendar-day">
			<div class="week-calendar-day-name week-calendar-day-cell week-calendar-heading-cell">
				{getDayName(day)}
			</div>
			<div
				class="week-calendar-day-meal week-calendar-day-cell"
				use:clickOutside
				on:click_outside={() => handleClickOutsideCell(`${day}-${true}`)}
			>
				<MealInput
					value={weekMeals ? getMeal(day, true) : ''}
					title={weekMeals ? getMeal(day, true) : null}
					suggestions={suggestions.input === `${day}-${true}` ? suggestions.suggestions : null}
					on:valueChange={(event) => onInputChange(event.detail.value, day, true)}
					on:enterPressed={() => saveMeal(day, true)}
					on:selectSuggestion={(event) => onSelectSuggestion(day, true, event.detail.description)}
				/>
				<button class="week-calendar-day-cell-save" on:click={() => saveMeal(day, true)}>
					{@html check}
				</button>
			</div>
			<div class="week-calendar-day-cell week-calendar-day-actions">
				<button on:click={() => unlinkMeal(day, true)}>
					{@html bin}
				</button>
			</div>
			<div
				class="week-calendar-day-meal week-calendar-day-cell"
				use:clickOutside
				on:click_outside={() => handleClickOutsideCell(`${day}-${false}`)}
			>
				<MealInput
					value={weekMeals ? getMeal(day, false) : ''}
					title={weekMeals ? getMeal(day, false) : null}
					suggestions={suggestions.input === `${day}-${false}` ? suggestions.suggestions : null}
					on:valueChange={(event) => onInputChange(event.detail.value, day, false)}
					on:enterPressed={() => saveMeal(day, false)}
					on:selectSuggestion={(event) => onSelectSuggestion(day, false, event.detail.description)}
				/>
				<button class="week-calendar-day-cell-save" on:click={() => saveMeal(day, false)}>
					{@html check}
				</button>
			</div>
			<div class="week-calendar-day-cell week-calendar-day-actions">
				<button on:click={() => unlinkMeal(day, false)}>
					{@html bin}
				</button>
			</div>
		</div>
	{/each}
</div>

{#if snackBarVisible}
	<SnackBar message={snackBarContent} status={snackBarStatus} />
{/if}

<style lang="scss">
	.week-calendar {
		&-wrapper {
			width: 100%;
			border: 1px solid #96bbea;
			box-shadow: 0 2px 6px rgba(var(--color-theme-1), 0.5);
			border-radius: 4px;
			background: #fff;
		}
		&-day {
			display: flex;
			width: 100%;
			border-bottom: 1px solid var(--color-theme-1);
			height: 100px;
			&:last-child {
				border-bottom: none;
			}

			&-cell {
				height: 100%;
				flex: 5;
				display: flex;
				justify-content: center;
				align-items: center;
				border-left: 1px solid var(--color-theme-1);
				position: relative;

				&-save {
					opacity: 0;
					pointer-events: none;
					margin-right: 1rem;
					color: var(--color-theme-1);
					background: transparent;
					border: none;
					height: 32px;
					width: 32px;
					border-radius: 32px;
					cursor: pointer;
					visibility: hidden;
					display: flex;
					justify-content: center;
					align-items: center;
					&:hover,
					&:focus {
						filter: brightness(1.1);
					}
					&:active {
						filter: brightness(0.9);
					}
				}
				&:focus-within {
					.week-calendar-day-cell-save {
						visibility: visible;
						opacity: 1;
						pointer-events: auto;
					}
				}
			}
			&-name {
				flex: 0 0 150px;
			}
			&-actions {
				flex: 1;
				button {
					background: transparent;
					border-radius: 50px;
					color: var(--color-theme-1);
					border: none;
					transition: 0.2s ease-in;
					height: 50px;
					width: 50px;
					cursor: pointer;
					display: inline-flex;
					justify-content: center;
					align-items: center;
					&:focus,
					&:hover {
						background: rgba(var(--color-theme-1), 0.2);
					}
					&:active {
						background: rgba(var(--color-theme-1), 0.5);
						color: #fff;
					}
				}
			}
		}
		&-heading {
			&-cell {
				background: var(--color-theme-1);
				color: #fff;
			}
			&-row {
				max-height: 50px;
			}
		}
	}
</style>
