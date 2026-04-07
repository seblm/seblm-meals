<script lang="ts">
	import type { Meal } from '$lib/model/WeekMeals';
	import { resolve } from '$app/paths';

	interface Props {
		meal: Meal;
	}

	let { meal }: Props = $props();
</script>

<a href={resolve('/meal/[id]', { id: meal.id })}>{meal.description}</a>
{#if meal.url}
	<a
		class="icon fa7-regular--share-square"
		href={meal.url}
		rel="external"
		target="_blank"
		title="externalUrl"
	></a>
{/if}

<style>
	@import '../../routes/icon-share-square.css';
	.icon {
		height: 1rem;
		width: 1rem;
		vertical-align: top;
	}
</style>
