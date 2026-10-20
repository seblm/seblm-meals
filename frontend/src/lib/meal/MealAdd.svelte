<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.ts';
	import * as Card from '#lib/components/ui/card/index.ts';
	import type { MealSuggest } from '#lib/model/WeekMeals.ts';
	import Plus from '@lucide/svelte/icons/plus';

	interface Props {
		link: (time: string) => Promise<void>;
		mealSuggest: MealSuggest;
		special?: string;
	}

	let { link, mealSuggest, special }: Props = $props();
</script>

<Card.Root>
	{#if mealSuggest.meal.image}
		<img src={mealSuggest.meal.image} alt={mealSuggest.meal.description} />
	{/if}
	<Card.Header>
		<Card.Title>
			{#if special}<strong>{special}</strong><br />{/if}
			{@html mealSuggest.descriptionLabel}<br />
			used {mealSuggest.count} time{#if mealSuggest.count > 1}s{/if}<br />
			since {mealSuggest.lastUsed} days
		</Card.Title>
		<Card.Action>
			<Button size="icon" class="rounded-full" onclick={() => link(mealSuggest.meal.description)}>
				<Plus class="h-4 w-4" />
			</Button>
		</Card.Action>
	</Card.Header>
</Card.Root>
