<script lang="ts">
	import { resolve } from '$app/paths';
	import { Button, buttonVariants } from '#lib/components/ui/button/index.ts';
	import * as Drawer from '#lib/components/ui/drawer/index.ts';
	import * as Card from '#lib/components/ui/card/index.ts';
	import type { MealEntry } from '#lib/model/WeekMeals.ts';
	import Plus from '@lucide/svelte/icons/plus';
	import Trash from '@lucide/svelte/icons/trash';

	interface Props {
		mealEntry?: MealEntry;
		reference: string;
		unlink: () => Promise<void>;
	}

	let { mealEntry, reference, unlink }: Props = $props();

	let day = $derived(reference.slice(8, 10));
	let month = $derived(reference.slice(5, 7));
	let year = $derived(reference.slice(0, 4));
</script>

<Card.Root>
	{#if mealEntry?.meal.image}
		<img src={mealEntry.meal.image} alt={mealEntry?.meal.description} />
	{/if}
	{#if mealEntry}
		<Card.Header>
			<Card.Title>{mealEntry.meal.description}</Card.Title>
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
							<Button onclick={unlink}>OK</Button>
							<Drawer.Close class={buttonVariants({ variant: 'outline' })}>Cancel</Drawer.Close>
						</Drawer.Footer>
					</Drawer.Content>
				</Drawer.Root>
			</Card.Action>
		</Card.Header>
	{:else}
		<Card.Footer class="grow justify-end">
			<Button href={resolve('/meals/[year]-[month]-[day]/link', { day, month, year })}
				><Plus class="h-4 w-4" /></Button
			>
		</Card.Footer>
	{/if}
</Card.Root>
