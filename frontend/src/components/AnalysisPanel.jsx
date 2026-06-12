import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';

import { analyzeLocation, getLatestScore, getNearby } from '../api/analysis';

const scoreMetrics = [
  ['footTrafficScore', 'Foot traffic'],
  ['transportScore', 'Transport'],
  ['demandScore', 'Demand'],
  ['competitionScore', 'Competition'],
  ['rentScore', 'Rent'],
];

export default function AnalysisPanel({
  accessToken,
  location,
  radius,
  showDetailLink = false,
  onAnalysisComplete,
}) {
  const queryClient = useQueryClient();
  const locationId = location?.id;
  const isEnabled = Boolean(accessToken && locationId);

  const nearbyQuery = useQuery({
    queryKey: ['location-nearby', accessToken, locationId, radius],
    queryFn: () => getNearby(locationId, radius, accessToken),
    enabled: isEnabled,
  });

  const scoreQuery = useQuery({
    queryKey: ['location-score', accessToken, locationId],
    queryFn: () => getLatestScore(locationId, accessToken),
    enabled: isEnabled,
    retry: false,
  });

  const analysisMutation = useMutation({
    mutationFn: () => analyzeLocation(locationId, radius, accessToken),
    onSuccess: async (score) => {
      queryClient.setQueryData(['location-score', accessToken, locationId], score);
      await queryClient.invalidateQueries({
        queryKey: ['location-nearby', accessToken, locationId],
      });
      onAnalysisComplete?.(score);
    },
  });

  if (!location) {
    return (
      <StateCard
        title="Select a candidate location"
        tone="empty"
        body="Marker clicks and list selections open analysis details here."
      />
    );
  }

  const score =
    analysisMutation.data ?? (scoreQuery.isError ? null : scoreQuery.data);
  const nearby = nearbyQuery.data;

  return (
    <div className="space-y-4">
      <section className="rounded-md border border-zinc-200 bg-zinc-50 p-4">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <p className="text-sm font-semibold text-zinc-950">{location.name}</p>
            <p className="mt-1 text-sm leading-5 text-zinc-600">
              {location.address}
            </p>
            <p className="mt-2 text-xs font-semibold uppercase tracking-wide text-zinc-500">
              {location.businessType}
            </p>
          </div>
          <ScoreBadge score={score?.totalScore} />
        </div>

        <div className="mt-4 flex flex-wrap items-center gap-2">
          <button
            className="rounded-md bg-zinc-950 px-3 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:bg-zinc-400"
            type="button"
            disabled={analysisMutation.isPending}
            onClick={() => analysisMutation.mutate()}
          >
            {analysisMutation.isPending ? 'Analyzing...' : `Analyze ${radius}m`}
          </button>

          {showDetailLink ? (
            <Link
              className="rounded-md border border-zinc-300 px-3 py-2 text-sm font-semibold text-zinc-800 hover:border-teal-600"
              to={`/locations/${location.id}`}
            >
              Open detail page
            </Link>
          ) : null}
        </div>

        <dl className="mt-4 grid grid-cols-2 gap-3 text-sm">
          <Metric label="Radius" value={`${radius}m`} />
          <Metric label="Last analyzed" value={formatDate(score?.calculatedAt)} />
        </dl>
      </section>

      {analysisMutation.isError ? (
        <StateCard
          title="Analysis failed"
          tone="warning"
          body={analysisMutation.error.message}
        />
      ) : null}

      {scoreQuery.isPending ? (
        <StateCard
          title="Loading latest score"
          tone="neutral"
          body="Checking the most recent analysis result."
        />
      ) : null}

      {scoreQuery.isError && !analysisMutation.data ? (
        <StateCard
          title="No saved score yet"
          tone="empty"
          body="Run analysis to calculate the latest score and explanation."
        />
      ) : null}

      {score ? (
        <>
          <ScoreChart score={score} />
          <section className="rounded-md border border-zinc-200 p-4">
            <p className="text-sm font-semibold text-zinc-950">
              Scoring explanation
            </p>
            <p className="mt-2 text-sm leading-6 text-zinc-700">
              {score.explanation || 'No explanation returned.'}
            </p>
          </section>
        </>
      ) : null}

      {nearbyQuery.isPending ? (
        <StateCard
          title="Loading nearby evidence"
          tone="neutral"
          body="Nearby facilities, competitors, and transit stops are loading."
        />
      ) : null}

      {nearbyQuery.isError ? (
        <StateCard
          title="Could not load nearby evidence"
          tone="warning"
          body={nearbyQuery.error.message}
        />
      ) : null}

      {nearby ? (
        <>
          <NearbySummary nearby={nearby} />
          <EvidenceTable
            title="Facilities"
            rows={nearby.facilities}
            columns={[
              ['name', 'Name'],
              ['category', 'Category'],
              ['address', 'Address'],
            ]}
          />
          <EvidenceTable
            title="Competitors"
            rows={nearby.competitors}
            columns={[
              ['name', 'Name'],
              ['businessType', 'Type'],
              ['address', 'Address'],
            ]}
          />
          <EvidenceTable
            title="Transit stops"
            rows={nearby.transitStops}
            columns={[
              ['name', 'Name'],
              ['type', 'Type'],
              ['latitude', 'Latitude'],
              ['longitude', 'Longitude'],
            ]}
          />
        </>
      ) : null}
    </div>
  );
}

function ScoreBadge({ score }) {
  return (
    <div className={`rounded-md px-3 py-2 text-center ${getScoreBadgeClass(score)}`}>
      <p className="text-xs font-semibold uppercase tracking-wide">Score</p>
      <p className="text-2xl font-bold">{formatScore(score)}</p>
    </div>
  );
}

function ScoreChart({ score }) {
  return (
    <section className="rounded-md border border-zinc-200 p-4">
      <div className="flex items-center justify-between gap-3">
        <p className="text-sm font-semibold text-zinc-950">Sub-scores</p>
        <p className="text-sm font-bold text-zinc-900">
          Total {formatScore(score.totalScore)}
        </p>
      </div>
      <div className="mt-4 space-y-3">
        {scoreMetrics.map(([key, label]) => {
          const value = toScoreNumber(score[key]);

          return (
            <div className="grid gap-1" key={key}>
              <div className="flex justify-between text-xs font-semibold text-zinc-600">
                <span>{label}</span>
                <span>{formatScore(value)}</span>
              </div>
              <div className="h-2 overflow-hidden rounded-full bg-zinc-200">
                <div
                  className="h-full rounded-full bg-teal-700"
                  style={{ width: `${value ?? 0}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

function NearbySummary({ nearby }) {
  const counts = nearby.counts ?? {};

  return (
    <section className="grid grid-cols-2 gap-3 text-sm">
      <Metric label="Transit stops" value={counts.transitStopCount ?? 0} />
      <Metric label="Subway stations" value={counts.subwayStationCount ?? 0} />
      <Metric label="Bus stops" value={counts.busStopCount ?? 0} />
      <Metric label="Facilities" value={counts.demandFacilityCount ?? 0} />
      <Metric label="Competitors" value={counts.competitorCount ?? 0} />
      <Metric
        label="Avg foot traffic"
        value={formatNumber(nearby.footTraffic?.averageCount)}
      />
    </section>
  );
}

function EvidenceTable({ title, rows = [], columns }) {
  return (
    <section className="overflow-hidden rounded-md border border-zinc-200">
      <div className="flex items-center justify-between border-b border-zinc-200 bg-zinc-50 px-3 py-2">
        <p className="text-sm font-semibold text-zinc-950">{title}</p>
        <p className="text-xs font-semibold text-zinc-500">{rows.length} rows</p>
      </div>

      {rows.length === 0 ? (
        <p className="p-3 text-sm text-zinc-600">No nearby records found.</p>
      ) : (
        <div className="max-h-56 overflow-auto">
          <table className="min-w-full divide-y divide-zinc-200 text-left text-sm">
            <thead className="bg-white text-xs font-semibold uppercase tracking-wide text-zinc-500">
              <tr>
                {columns.map(([, label]) => (
                  <th className="px-3 py-2" key={label} scope="col">
                    {label}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-100">
              {rows.map((row) => (
                <tr key={row.id}>
                  {columns.map(([key]) => (
                    <td className="max-w-[220px] px-3 py-2 text-zinc-700" key={key}>
                      <span className="line-clamp-2">{formatCell(row[key])}</span>
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

function Metric({ label, value }) {
  return (
    <div className="rounded-md border border-zinc-200 bg-white p-3">
      <dt className="text-xs font-medium uppercase tracking-wide text-zinc-500">
        {label}
      </dt>
      <dd className="mt-2 font-semibold text-zinc-950">{value}</dd>
    </div>
  );
}

function StateCard({ title, body, tone }) {
  const toneClass = {
    neutral: 'border-zinc-200 bg-zinc-50 text-zinc-700',
    warning: 'border-amber-200 bg-amber-50 text-amber-900',
    empty: 'border-teal-200 bg-teal-50 text-teal-900',
  }[tone];

  return (
    <div className={`rounded-md border p-3 ${toneClass}`}>
      <p className="text-sm font-semibold">{title}</p>
      <p className="mt-1 text-sm leading-5">{body}</p>
    </div>
  );
}

function getScoreBadgeClass(score) {
  const value = toScoreNumber(score);

  if (value == null) {
    return 'bg-zinc-100 text-zinc-700';
  }

  if (value >= 80) {
    return 'bg-teal-100 text-teal-900';
  }

  if (value >= 60) {
    return 'bg-amber-100 text-amber-900';
  }

  return 'bg-rose-100 text-rose-900';
}

function toScoreNumber(value) {
  const score = Number(value);

  return Number.isFinite(score) ? Math.max(0, Math.min(100, score)) : null;
}

function formatScore(score) {
  const value = toScoreNumber(score);

  return value == null ? '--' : Math.round(value).toString();
}

function formatDate(value) {
  if (!value) {
    return '--';
  }

  const date = new Date(value);

  return Number.isNaN(date.getTime()) ? '--' : date.toLocaleString();
}

function formatNumber(value) {
  const number = Number(value);

  return Number.isFinite(number) ? number.toLocaleString() : '0';
}

function formatCell(value) {
  if (value == null || value === '') {
    return '--';
  }

  if (typeof value === 'number') {
    return value.toLocaleString();
  }

  return String(value);
}
