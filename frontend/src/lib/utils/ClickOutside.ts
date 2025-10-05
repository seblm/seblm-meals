import type { ActionReturn } from 'svelte/action';

export function clickOutside(node: HTMLElement): ActionReturn {
	const handleClick = (event: Event) => {
		console.log(`node: ${node}; event.target: ${event.target}`);
		const target = event.target as Node;
		if (node && (node === target || !node.contains(target)) && !event.defaultPrevented) {
			node.dispatchEvent(new CustomEvent('click_outside'));
		}
	};

	document.addEventListener('click', handleClick, true);

	return {
		destroy() {
			document.removeEventListener('click', handleClick, true);
		}
	};
}
