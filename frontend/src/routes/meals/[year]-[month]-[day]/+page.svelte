<script lang="ts">
	import type { PageProps } from './$types';
	import { Button, buttonVariants } from '$lib/components/ui/button';
	import * as Drawer from "$lib/components/ui/drawer";
	import * as Card from '$lib/components/ui/card';
	import ChevronLeft from '@lucide/svelte/icons/chevron-left';
	import ChevronRight from '@lucide/svelte/icons/chevron-right';
	import ListIndentIncrease from '@lucide/svelte/icons/list-indent-increase';
	import Plus from '@lucide/svelte/icons/plus';
	import Trash from '@lucide/svelte/icons/trash';

	let { data }: PageProps = $props();
</script>

<div class="min-h-screen bg-background">
	<Button size="icon" class="fixed top-4 left-4 z-0 rounded-full h-14 w-14 bg-background/60 backdrop-blur-xl shadow-lg">
		<ChevronLeft />
	</Button>
	<Button size="icon" class="fixed top-4 right-4 z-50 rounded-full h-14 w-14 bg-background/60 backdrop-blur-xl shadow-lg">
		<ChevronRight />
	</Button>
	<Button size="icon" class="fixed bottom-4 right-4 z-50 rounded-full h-14 w-14 bg-primary shadow-xl">
		<ListIndentIncrease />
	</Button>
	<main class="px-4 pt-20 pb-24 space-y-10" data-sveltekit-preload-data="tap">
		{#each data.days as day (day.reference)}
			<section>
				<h2 class="mb-4 text-xl font-bold">{day.reference}</h2>
				<div class="grid grid-cols-2 gap-4">
					{#each [day.lunch, day.dinner] as mealEntry}
						<Card.Root>
							{#if mealEntry?.meal.image}
								<img src="{mealEntry?.meal.image}" alt={mealEntry?.meal.description} />
							{/if}
							<Card.Header>
								{#if mealEntry?.meal.description}
									<Card.Title>{mealEntry?.meal.description}</Card.Title>
								{/if}
								{#if mealEntry}
									<Card.Action>
										<Drawer.Root>
											<Drawer.Trigger>
												<Button size="icon" variant="secondary" class="rounded-full">
													<Trash class="h-4 w-4" />
												</Button>
											</Drawer.Trigger>
											<Drawer.Content>
												<Drawer.Header>
													<Drawer.Title>Are you sure you want to remove {mealEntry?.meal.description}?</Drawer.Title>
												</Drawer.Header>
												<Drawer.Footer>
													<Button>OK</Button>
													<Drawer.Close class={buttonVariants({ variant: "outline" })}>Cancel</Drawer.Close>
												</Drawer.Footer>
											</Drawer.Content>
										</Drawer.Root>
									</Card.Action>
								{/if}
							</Card.Header>
							{#if !mealEntry}
								<Card.Footer class="grow justify-end">
									<Button><Plus class="h-4 w-4" /></Button>
								</Card.Footer>
							{/if}
						</Card.Root>
					{/each}
				</div>
			</section>
		{/each}
	</main>
</div>