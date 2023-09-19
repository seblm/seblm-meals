<script lang="ts">
	import type { SearchSuggestion } from '$lib/model/WeekMeals';
	import { createEventDispatcher } from 'svelte';

	export let suggestion: SearchSuggestion;
	export let lastUsed: string;

	const dispatch = createEventDispatcher();

	function handleClick() {
		dispatch('select');
	}
</script>

<li on:click={handleClick}>
	<span title={suggestion.description}>{@html suggestion.descriptionLabel} </span>
	<span> ({suggestion.count})</span>
	<div class="spacer" />
	<span>{lastUsed}</span>
</li>

<style lang="scss">
	li {
		list-style: none;
		margin: 0;
		padding: 0.5rem;
		cursor: pointer;
		transition: 0.2s ease-in;
		display: flex;
		border-left: 0.25rem solid var(--color-theme-2);
		border-right: 0.25rem solid var(--color-theme-2);
		font-weight: bold;
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
</style>
