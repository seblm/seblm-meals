<script lang="ts">
	import { externalLink } from '$lib/images/external-link';
	import MealDate from '$lib/MealDate.svelte';
	import MealMenu from '$lib/MealMenu.svelte';
	import type { PageProps } from './$types';

	let { data }: PageProps = $props();
</script>

<main class="container" data-sveltekit-preload-data="tap">
	<MealMenu year={2025} week={44} />
	<h2>
		{data.meal.description} ({data.count})
		{#if data.meal.url}
			{@const href = data.meal.url}
			<a {href} target="_blank">{@html externalLink}</a>
		{/if}
	</h2>
	<ul>
		<li>Last usage: <MealDate date={data.last} /></li>
		<li>First usage: <MealDate date={data.first} /></li>
	</ul>
</main>
