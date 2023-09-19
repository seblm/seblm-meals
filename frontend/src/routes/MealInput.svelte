<script lang="ts">
	import type { SuggestionResponse } from '$lib/model/WeekMeals';
	import { createEventDispatcher } from 'svelte';
	import MealInputSpecialSuggestionItem from './MealInputSpecialSuggestionItem.svelte';
	import MealInputSuggestionItem from './MealInputSuggestionItem.svelte';

	export let value: string;
	export let title: string;
	export let suggestions: SuggestionResponse | null;
	$: mostRecents = suggestions?.mostRecents;
	$: yearAgo = suggestions?.fiftyTwoWeeksAgo;
	$: monthAgo = suggestions?.fourWeeksAgo;
	$: showSuggestions = suggestions;

	const dispatch = createEventDispatcher();
	function handleInputChange(event: Event) {
		const value = (event.target as HTMLInputElement).value;
		dispatch('valueChange', { value });
	}

	function handleEnterPress(keyEvent) {
		if (keyEvent.code === 'Enter') {
			dispatch('enterPressed');
		}
	}

	function onSelectSuggestion(description: string) {
		dispatch('selectSuggestion', { description });
	}
</script>

<input
	type="text"
	{value}
	{title}
	on:input={handleInputChange}
	on:keypress={handleEnterPress}
	on:focus={handleInputChange}
/>
{#if showSuggestions}
	<ul class="suggestions" class:suggestions-visible={showSuggestions}>
		{#if yearAgo}
			<MealInputSpecialSuggestionItem
				suggestion={yearAgo}
				on:select={() => onSelectSuggestion(yearAgo.description)}
				lastUsed="Il y a 1 an"
			/>
		{/if}
		{#if monthAgo}
			<MealInputSpecialSuggestionItem
				suggestion={monthAgo}
				on:select={() => onSelectSuggestion(monthAgo.description)}
				lastUsed="Il y a 1 mois"
			/>
		{/if}
		{#each suggestions.mostRecents ?? [] as suggestion}
			<MealInputSuggestionItem
				{suggestion}
				on:select={() => onSelectSuggestion(suggestion.description)}
			/>
		{/each}
	</ul>
{/if}

<style lang="scss">
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
	.suggestions {
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
		max-height: 300px;
		overflow-y: auto;
		transform: translateY(-2.5rem);
		&-visible {
			display: block;
		}
	}
</style>
