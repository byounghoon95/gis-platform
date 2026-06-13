import { useQuery } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { useState } from 'react';

import { getAdminLocation } from '../api/locations';
import { useAuthStore } from '../auth/authStore';
import AnalysisPanel from '../components/AnalysisPanel';

const radiusOptions = [300, 500, 1000];

export default function LocationDetailPage() {
  const { id } = useParams();
  const accessToken = useAuthStore((state) => state.accessToken);
  const [radius, setRadius] = useState(500);

  const locationQuery = useQuery({
    queryKey: ['admin-location', accessToken, id],
    queryFn: () => getAdminLocation(id, accessToken),
    enabled: Boolean(accessToken && id),
  });

  return (
    <main className="min-h-screen bg-zinc-100 text-zinc-950">
      <header className="border-b border-zinc-200 bg-white">
        <div className="mx-auto flex max-w-6xl flex-col gap-4 px-4 py-4 sm:px-6 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-teal-700">
              Location detail
            </p>
            <h1 className="mt-1 text-2xl font-semibold">
              {locationQuery.data?.name ?? 'Analysis report'}
            </h1>
          </div>

          <nav className="flex flex-wrap items-center gap-2 text-sm font-medium">
            <Link
              className="rounded-md border border-zinc-300 px-3 py-2 text-zinc-700 hover:border-teal-600"
              to="/dashboard"
            >
              Back to dashboard
            </Link>
          </nav>
        </div>
      </header>

      <div className="mx-auto grid max-w-6xl gap-4 px-4 py-4 sm:px-6 lg:grid-cols-[260px_minmax(0,1fr)]">
        <aside className="h-fit rounded-lg border border-zinc-200 bg-white p-4 shadow-sm">
          <p className="text-sm font-semibold text-zinc-950">Analysis radius</p>
          <div className="mt-3 grid grid-cols-3 rounded-md border border-zinc-300 bg-zinc-50 p-1 lg:grid-cols-1">
            {radiusOptions.map((option) => (
              <label
                className="cursor-pointer rounded px-2 py-1.5 text-center text-sm has-[:checked]:bg-zinc-950 has-[:checked]:text-white"
                key={option}
              >
                <input
                  className="sr-only"
                  type="radio"
                  name="detail-radius"
                  checked={radius === option}
                  onChange={() => setRadius(option)}
                />
                {option}m
              </label>
            ))}
          </div>

          {locationQuery.data ? (
            <dl className="mt-4 space-y-3 text-sm">
              <Metric label="Business type" value={locationQuery.data.businessType} />
              <Metric label="Latitude" value={formatCoordinate(locationQuery.data.latitude)} />
              <Metric label="Longitude" value={formatCoordinate(locationQuery.data.longitude)} />
            </dl>
          ) : null}
        </aside>

        <section className="rounded-lg border border-zinc-200 bg-white p-4 shadow-sm">
          {locationQuery.isPending ? (
            <StateCard
              title="Loading location"
              tone="neutral"
              body="Fetching the selected candidate location."
            />
          ) : null}

          {locationQuery.isError ? (
            <StateCard
              title="Could not load location"
              tone="warning"
              body={locationQuery.error.message}
            />
          ) : null}

          {locationQuery.data ? (
            <AnalysisPanel
              accessToken={accessToken}
              location={locationQuery.data}
              radius={radius}
            />
          ) : null}
        </section>
      </div>
    </main>
  );
}

function Metric({ label, value }) {
  return (
    <div>
      <dt className="text-xs font-medium uppercase tracking-wide text-zinc-500">
        {label}
      </dt>
      <dd className="mt-1 font-semibold text-zinc-950">{value || '--'}</dd>
    </div>
  );
}

function StateCard({ title, body, tone }) {
  const toneClass = {
    neutral: 'border-zinc-200 bg-zinc-50 text-zinc-700',
    warning: 'border-amber-200 bg-amber-50 text-amber-900',
  }[tone];

  return (
    <div className={`rounded-md border p-3 ${toneClass}`}>
      <p className="text-sm font-semibold">{title}</p>
      <p className="mt-1 text-sm leading-5">{body}</p>
    </div>
  );
}

function formatCoordinate(value) {
  const number = Number(value);

  return Number.isFinite(number) ? number.toFixed(6) : '';
}
