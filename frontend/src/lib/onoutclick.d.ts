import 'svelte/elements';

/*
  This is the way to avoid this error message when running npm run check:
  Object literal may only specify known properties, and '"onoutclick"' does not exist in type 'Omit<HTMLAttributes<HTMLDivElement>, never> & HTMLAttributes<any> & Record<never, any>'. (ts)
  From https://stackoverflow.com/a/79093361/2436568.
*/
declare module 'svelte/elements' {
	export interface DOMAttributes<T extends EventTarget> {
		onoutclick?: (event: CustomEvent<T>) => void;
	}
}
