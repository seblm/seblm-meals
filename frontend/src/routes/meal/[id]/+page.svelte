<script lang="ts">
	import MealDate from '$lib/MealDate.svelte';
	import MealMenu from '$lib/MealMenu.svelte';
	import type { PageProps } from './$types';

	let { data }: PageProps = $props();
</script>

<main class="container" data-sveltekit-preload-data="tap">
	<MealMenu year={2026} week={12} />
	<h2>
		{data.meal.description} ({data.count})
		{#if data.meal.url}
			<a
				class="fa7-regular--share-square"
				href={data.meal.url}
				rel="external"
				target="_blank"
				title="externalLink"
			>
			</a>
		{/if}
	</h2>
	<ul>
		<li>Last usage: <MealDate date={data.last} /></li>
		<li>First usage: <MealDate date={data.first} /></li>
	</ul>
</main>

<style>
	@import '../../icon-share-square.css';
</style>
