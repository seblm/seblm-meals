<script lang="ts">
	import { externalLink } from '$lib/images/external-link';
	import type { SuggestionResponse } from '$lib/model/WeekMeals';
	import MealInputSpecialSuggestionItem from './MealInputSpecialSuggestionItem.svelte';
	import MealInputSuggestionItem from './MealInputSuggestionItem.svelte';
	import type { FormEventHandler } from 'svelte/elements';

	interface Props {
		value: string;
		title: string;
		url: string | undefined;
		suggestions: SuggestionResponse | null;
		oninput: FormEventHandler<HTMLInputElement>;
		onEnterPressed: () => void;
		onSelectSuggestion: (description: string) => void;
	}
	let { value, title, url, suggestions, oninput, onEnterPressed, onSelectSuggestion }: Props =
		$props();
	let mostRecents = $derived(suggestions?.mostRecents);
	let yearAgo = $derived(suggestions?.fiftyTwoWeeksAgo);
	let monthAgo = $derived(suggestions?.fourWeeksAgo);
	let showSuggestions = $derived(suggestions);

	function handleEnterPress(keyEvent: KeyboardEvent) {
		if (keyEvent.code === 'Enter') {
			onEnterPressed();
		}
	}
</script>

<input type="text" {value} {title} {oninput} onkeypress={handleEnterPress} onfocus={oninput} />
{#if url}
	{@const href = url}
	<a {href} target="_blank">{@html externalLink}</a>
{/if}
{#if showSuggestions}
	<ul class="suggestions" class:suggestions-visible={showSuggestions}>
		{#if yearAgo}
			<MealInputSpecialSuggestionItem
				suggestion={yearAgo}
				onclick={() => onSelectSuggestion(yearAgo.description)}
				lastUsed="Il y a 1 an"
			/>
		{/if}
		{#if monthAgo}
			<MealInputSpecialSuggestionItem
				suggestion={monthAgo}
				onclick={() => onSelectSuggestion(monthAgo.description)}
				lastUsed="Il y a 1 mois"
			/>
		{/if}
		{#each mostRecents ?? [] as suggestion (suggestion.description)}
			<MealInputSuggestionItem
				{suggestion}
				onclick={() => onSelectSuggestion(suggestion.description)}
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
		box-shadow:
			0 1px 3px rgba(0, 0, 0, 0.2),
			0 2px 6px rgba(0, 0, 0, 0.1);
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
