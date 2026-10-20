<script lang="ts">
	import { Button, buttonVariants } from '#lib/components/ui/button/index.ts';
	import * as Drawer from '#lib/components/ui/drawer/index.ts';
	import * as Card from '#lib/components/ui/card/index.ts';
	import type { MealEntry } from '#lib/model/WeekMeals.ts';
	import Plus from '@lucide/svelte/icons/plus';
	import Trash from '@lucide/svelte/icons/trash';

	interface Props {
		mealEntryOrLink: MealEntry | string;
		unlink: (time: string) => Promise<void>;
	}

	let { mealEntryOrLink, unlink }: Props = $props();
</script>

{#snippet mealEntry(mealEntry: MealEntry)}
	{#if mealEntry.meal.image}
		<img src={mealEntry.meal.image} alt={mealEntry.meal.description} />
	{/if}
	<Card.Header>
		<Card.Title>
			{#if mealEntry.meal.url}
				<a href={mealEntry.meal.url} target="_blank">{mealEntry.meal.description}</a>
			{:else}
				{mealEntry.meal.description}
			{/if}
		</Card.Title>
		<Card.Action>
			<Drawer.Root>
				<Drawer.Trigger>
					<Button size="icon" variant="destructive" class="rounded-full">
						<Trash class="h-4 w-4" />
					</Button>
				</Drawer.Trigger>
				<Drawer.Content>
					<Drawer.Header>
						<Drawer.Title>
							Are you sure you want to remove {mealEntry.meal.description}?
						</Drawer.Title>
					</Drawer.Header>
					<Drawer.Footer>
						<Button onclick={() => unlink(mealEntry.time)}>OK</Button>
						<Drawer.Close class={buttonVariants({ variant: 'outline' })}>Cancel</Drawer.Close>
					</Drawer.Footer>
				</Drawer.Content>
			</Drawer.Root>
		</Card.Action>
	</Card.Header>
{/snippet}

{#snippet addMealEntry(link: string)}
	<Card.Footer class="grow justify-end">
		<Button href={link}>
			<Plus class="h-4 w-4" />
		</Button>
	</Card.Footer>
{/snippet}

<Card.Root>
	{#if typeof mealEntryOrLink !== 'string'}
		{@render mealEntry(mealEntryOrLink)}
	{:else}
		{@render addMealEntry(mealEntryOrLink)}
	{/if}
</Card.Root>
