import { DateTime } from 'luxon';

export class DateUtils {
  public getUtcOffset(ianaTimezone: string): string {
    const offset = DateTime.now().setZone(ianaTimezone).toFormat('ZZ'); // e.g., "+02:00"
    return offset.replace(':', ''); // Angular wants "+0200" format
  }

  public getFlightDuration(departure: string, arrival: string): string {
    const dep = new Date(departure);
    const arr = new Date(arrival);

    const diffMs = arr.getTime() - dep.getTime();
    const diffMins = Math.floor(diffMs / 60000);

    const hours = Math.floor(diffMins / 60);
    const minutes = diffMins % 60;

    const pad = (n: number) => n.toString().padStart(2, '0');

    return `${pad(hours)}h ${pad(minutes)}m`;
  }
}
