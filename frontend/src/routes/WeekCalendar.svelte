<script lang="ts">
	import { check } from '$lib/images/check';
	import shuffle from '$lib/images/shuffle';
	import type { Day, SeachSuggestions, WeekMeals } from '$lib/model/WeekMeals';
	import { date } from '$lib/stores';
	import { getSuggestions, getWeekMeals, linkOrInsert } from '$lib/utils/api';
	import { getDayName } from '$lib/utils/functions';
	import { getWeek, getYear } from 'date-fns';
	import { addWeeks } from 'date-fns/fp';
	import { onDestroy, onMount } from 'svelte';
	import { blur } from 'svelte/transition';
	import CalendarNavigation from './CalendarNavigation.svelte';
	import SnackBar from './SnackBar.svelte';
	import { clickOutside } from '$lib/utils/ClickOutside';

	const days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];

	interface InputValue {
		day: string;
		isLunch: boolean;
		mealDescription: string;
	}

	interface SuggestionList {
		input: string;
		suggestions: SeachSuggestions[];
	}

	let selectedDate: Date;
	let weekMeals: WeekMeals;
	let inputValues: Record<string, InputValue> = {};
	let snackBarVisible = false;
	let snackBarContent = '';
	let snackBarStatus: 'success' | 'error' = 'success';
	const suggestions: SuggestionList = {
		input: '',
		suggestions: []
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
			suggestions.suggestions = response.mostRecents;
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
		suggestions.suggestions = [];
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
				<input
					type="text"
					value={weekMeals ? getMeal(day, true) : ''}
					title={weekMeals ? getMeal(day, true) : null}
					on:input={(inputEvent) => onInputChange(inputEvent.target.value, day, true)}
					on:keypress={(keyEvent) => {
						if(keyEvent.code === 'Enter') {
							saveMeal(day, true);
						}
					}}
				/>
				{#if suggestions && suggestions.input === `${day}-${true}`}
					<ul
						class="week-calendar-day-cell-suggestions"
						class:week-calendar-day-cell-suggestions-visible={suggestions.input ===
							`${day}-${true}`}
					>
						{#each suggestions.suggestions ?? [] as suggestion}
							<li on:click={() => onSelectSuggestion(day, true, suggestion.description)}>
								<span title={suggestion.description}>{@html suggestion.descriptionLabel} </span>
								<span> ({suggestion.count})</span>
								<div class="spacer" />
								<span>{suggestion.lastused} jours</span>
							</li>
						{/each}
					</ul>
				{/if}
				<button class="week-calendar-day-cell-save" on:click={() => saveMeal(day, true)}>
					{@html check}
				</button>
			</div>
			<div class="week-calendar-day-cell week-calendar-day-actions">
				<button>
					{@html shuffle}
				</button>
			</div>
			<div
				class="week-calendar-day-meal week-calendar-day-cell"
				use:clickOutside
				on:click_outside={() => handleClickOutsideCell(`${day}-${false}`)}
			>
				<input
					type="text"
					value={weekMeals ? getMeal(day, false) : ''}
					title={weekMeals ? getMeal(day, false) : null}
					on:input={(inputEvent) => onInputChange(inputEvent.target.value, day, false)}
					on:keypress={(keyEvent) => {
						if(keyEvent.code === 'Enter') {
							saveMeal(day, false);
						}
					}}
				/>
				{#if suggestions && suggestions.input === `${day}-${false}`}
					<ul
						class="week-calendar-day-cell-suggestions"
						class:week-calendar-day-cell-suggestions-visible={suggestions.input ===
							`${day}-${false}`}
					>
						{#each suggestions.suggestions ?? [] as suggestion}
							<li on:click={() => onSelectSuggestion(day, false, suggestion.description)}>
								<span title={suggestion.description}>{@html suggestion.descriptionLabel} </span>
								<span> ({suggestion.count})</span>
								<div class="spacer" />
								<span>{suggestion.lastused} jours</span>
							</li>
						{/each}
					</ul>
				{/if}
				<button class="week-calendar-day-cell-save" on:click={() => saveMeal(day, false)}>
					{@html check}
				</button>
			</div>
			<div class="week-calendar-day-cell week-calendar-day-actions">
				<button>
					{@html shuffle}
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
				input[type='text'] {
					width: 100%;
					margin: auto 1rem;
					padding: 1rem;
					border: none;
					border-radius: 5px;
					box-shadow: inset 2px 3px 6px rgba(0, 0, 0, 0);
					transition: 0.2s ease-in;
					overflow: hidden;
					text-overflow: ellipsis;
					&:focus-visible {
						box-shadow: inset 2px 3px 6px var(--color-bg-0);
					}
				}
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
				&-suggestions {
					display: none;
					position: absolute;
					left: 0;
					top: 100%;
					width: 100%;
					background: #fff;
					border-radius: 5px;
					box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2), 0 2px 6px rgba(0, 0, 0, 0.1);
					z-index: 9;
					padding: 0;
					&-visible {
						display: block;
					}
					li {
						list-style: none;
						margin: 0;
						padding: 0.5rem 1rem;
						cursor: pointer;
						transition: 0.2s ease-in;
						display: flex;
						span {
							overflow: hidden;
							white-space: nowrap;
							text-overflow: ellipsis;
							&:first-child {
								max-width: 200px;
							}
							&:not(:last-child) {
								margin-right: 5px;
							}
						}
						&:hover {
							background: var(--color-bg-2);
						}
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
