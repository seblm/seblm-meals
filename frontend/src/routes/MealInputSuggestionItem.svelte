<script lang="ts">
	import type { SearchSuggestion } from '$lib/model/WeekMeals';

	interface Props {
		onclick: () => void;
		suggestion: SearchSuggestion;
	}
	let { onclick, suggestion }: Props = $props();
</script>

<li {onclick}>
	<span title={suggestion.description}>{@html suggestion.descriptionLabel} </span>
	<span> ({suggestion.count})</span>
	<div class="spacer"></div>
	<span>{suggestion.lastused} jours</span>
</li>

<style lang="scss">
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
</style>
