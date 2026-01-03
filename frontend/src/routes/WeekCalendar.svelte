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

	$: weekDays = weekMeals ? getWeekDays() : [];

	onMount(() => {
		updateWeekMeals();
	});

	const updateWeekMeals = () =>
		getWeekMeals(getYear(selectedDate), getWeek(selectedDate)).then((meals) => (weekMeals = meals));
	const getMealId = (dayString: string, isLunch: boolean): string => {
		let dayMeals = getDay(dayString);
		let id = isLunch ? (dayMeals?.lunch?.meal.id ?? '') : dayMeals?.dinner?.meal.id;
		return id ? id : '';
	};
	const getMealDescription = (dayString: string, isLunch: boolean): string => {
		let dayMeals = getDay(dayString);
		let description = isLunch
			? (dayMeals?.lunch?.meal.description ?? '')
			: dayMeals?.dinner?.meal.description;
		return description ? description : '';
	};
	const getUrl = (dayString: string, isLunch: boolean): string | undefined => {
		let dayMeals = getDay(dayString);
		return isLunch ? dayMeals?.lunch?.meal.url : dayMeals?.dinner?.meal.url;
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

	const onInputChange = (mealDescription: string, day: string, isLunch: boolean) => {
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

	const getWeekDays = () => {
		const weekDays: string[] = [];
		days.forEach((dayString) => {
			const dayReference = getDay(dayString)?.reference;
			const weekDay = dayReference?.substring(8, dayReference?.length);
			if (weekDay) {
				weekDays.push(weekDay);
			}
		});
		return weekDays;
	};

	onDestroy(unsubscribe);
</script>

{#if weekMeals}
	<div in:blur={{ delay: 200 }} out:blur={{ duration: 200 }}>
		<CalendarNavigation title={weekMeals.titles.long} onPrev={prevWeek} onNext={nextWeek} />
	</div>
{/if}
<div class="week-calendar-wrapper" in:blur={{ delay: 200 }} out:blur={{ duration: 200 }}>
	<div class="week-calendar-day week-calendar-heading-row">
		<div class="week-calendar-day-name week-calendar-day-cell week-calendar-heading-cell"></div>
		<div class="week-calendar-day-meal week-calendar-day-cell week-calendar-heading-cell">Midi</div>
		<div class="week-calendar-day-cell week-calendar-day-actions week-calendar-heading-cell"></div>
		<div class="week-calendar-day-meal week-calendar-day-cell week-calendar-heading-cell">Soir</div>
		<div class="week-calendar-day-cell week-calendar-day-actions week-calendar-heading-cell"></div>
	</div>
	{#each days as day, index (index)}
		<div class="week-calendar-day">
			<div class="week-calendar-day-name week-calendar-day-cell week-calendar-heading-cell">
				<p>{getDayName(day)}</p>
				<div class="week-calendar-day-number">{weekDays[index] ?? ''}</div>
			</div>
			<div
				class="week-calendar-day-meal week-calendar-day-cell"
				use:clickOutside
				onclick_outside={() => handleClickOutsideCell(`${day}-${true}`)}
			>
				<MealInput
					id={weekMeals ? getMealId(day, true) : undefined}
					value={weekMeals ? getMealDescription(day, true) : ''}
					url={weekMeals ? getUrl(day, true) : undefined}
					suggestions={suggestions.input === `${day}-${true}` ? suggestions.suggestions : null}
					oninput={(event) => onInputChange(event.currentTarget.value, day, true)}
					onEnterPressed={() => saveMeal(day, true)}
					onSelectSuggestion={(description) => onSelectSuggestion(day, true, description)}
				/>
				<button class="week-calendar-day-cell-save" onclick={() => saveMeal(day, true)}>
					{@html check}
				</button>
			</div>
			<div class="week-calendar-day-cell week-calendar-day-actions">
				<button onclick={() => unlinkMeal(day, true)}>
					{@html bin}
				</button>
			</div>

			<div
				class="week-calendar-day-meal week-calendar-day-cell"
				use:clickOutside
				onclick_outside={() => handleClickOutsideCell(`${day}-${false}`)}
			>
				<MealInput
					id={weekMeals ? getMealId(day, false) : undefined}
					value={weekMeals ? getMealDescription(day, false) : ''}
					url={weekMeals ? getUrl(day, false) : undefined}
					suggestions={suggestions.input === `${day}-${false}` ? suggestions.suggestions : null}
					oninput={(event) => onInputChange(event.currentTarget.value, day, false)}
					onEnterPressed={() => saveMeal(day, false)}
					onSelectSuggestion={(description) => onSelectSuggestion(day, false, description)}
				/>
				<button class="week-calendar-day-cell-save" onclick={() => saveMeal(day, false)}>
					{@html check}
				</button>
			</div>
			<div class="week-calendar-day-cell week-calendar-day-actions">
				<button onclick={() => unlinkMeal(day, false)}>
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
	@use '../lib/styles/variables';
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
				display: flex;
				flex-direction: column;
				align-items: center;
				p {
					margin: 0 0 0.25rem 0;
				}
			}
			&-number {
				font-weight: bold;
				font-size: 1.1em;
			}
			&-actions {
				flex: 1;
				min-width: 50px;
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
	@media screen and (max-width: variables.$screen-m) {
		.week-calendar {
			&-day {
				height: 140px;
				display: grid;
				grid-template-rows: repeat(2, 70px);
				grid-template-columns: 80px 1fr 50px;
				&-name {
					grid-column: 1 / span 1;
					grid-row: 1 / span 2;
					&:not(:last-of-type) {
						border-bottom: 1px solid var(--color-text);
					}
				}
				&-cell {
					&:nth-child(n + 4) {
						border-top: 1px solid var(--color-theme-1);
					}
				}
			}
			&-heading-row {
				display: none;
			}
		}
	}
</style>
