import { Router } from '@angular/router';

export class RouterUtils {
  static getCleanedUrl(router: Router, url: string): string {
    const urlTree = router.parseUrl(url);
    // Remove the 'step' query param
    delete urlTree.queryParams['step'];
    return urlTree.toString();
  }
}
