import { NavLink } from 'react-router-dom';

import { useAuthStore } from '../auth/authStore';

const radiusOptions = ['300m', '500m', '1000m'];
const businessTypes = ['All business types', 'Cafe', 'Restaurant', 'Retail'];
const scoreRanges = ['All scores', '80-100', '60-79', 'Under 60'];

const mapPins = [
  { id: 1, name: 'Gangnam Station', top: '38%', left: '58%', score: 86 },
  { id: 2, name: 'Yeouido Office', top: '48%', left: '37%', score: 72 },
  { id: 3, name: 'Hongdae Retail', top: '31%', left: '27%', score: 64 },
];

export default function DashboardPage() {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

  return (
    <main className="min-h-screen bg-zinc-100 text-zinc-950">
      <header className="border-b border-zinc-200 bg-white">
        <div className="dashboard-header mx-auto flex max-w-screen-2xl flex-col gap-4 px-4 py-4 sm:px-6">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-teal-700">
              GIS Platform
            </p>
            <h1 className="mt-1 text-2xl font-semibold">
              Location Analysis Dashboard
            </h1>
          </div>

          <nav className="flex flex-wrap items-center gap-2 text-sm font-medium">
            <NavLink
              to="/dashboard"
              className="rounded-md bg-zinc-950 px-3 py-2 text-white"
            >
              Dashboard
            </NavLink>
            <span className="rounded-md border border-zinc-200 px-3 py-2 text-zinc-600">
              {user?.name ?? user?.email ?? 'Signed in'}
            </span>
            <button
              className="rounded-md px-3 py-2 text-zinc-700 hover:bg-zinc-100"
              type="button"
              onClick={logout}
            >
              Sign out
            </button>
          </nav>
        </div>
      </header>

      <div className="mx-auto flex max-w-screen-2xl flex-col gap-4 px-4 py-4 sm:px-6">
        <FilterBar />

        <section className="dashboard-workspace grid min-h-[calc(100vh-190px)] gap-4">
          <CandidateList />
          <MapWorkspace />
          <DetailPanel />
        </section>
      </div>
    </main>
  );
}

function FilterBar() {
  return (
    <section className="dashboard-filter grid gap-3 rounded-lg border border-zinc-200 bg-white p-3 shadow-sm">
      <label className="text-sm font-medium text-zinc-700">
        Business type
        <select className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-zinc-950 outline-none focus:border-teal-700 focus:ring-2 focus:ring-teal-100">
          {businessTypes.map((option) => (
            <option key={option}>{option}</option>
          ))}
        </select>
      </label>

      <label className="text-sm font-medium text-zinc-700">
        Score range
        <select className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-zinc-950 outline-none focus:border-teal-700 focus:ring-2 focus:ring-teal-100">
          {scoreRanges.map((option) => (
            <option key={option}>{option}</option>
          ))}
        </select>
      </label>

      <fieldset className="text-sm font-medium text-zinc-700">
        <legend>Analysis radius</legend>
        <div className="mt-1 grid grid-cols-3 rounded-md border border-zinc-300 bg-zinc-50 p-1">
          {radiusOptions.map((option) => (
            <label
              className="cursor-pointer rounded px-2 py-1.5 text-center text-sm has-[:checked]:bg-zinc-950 has-[:checked]:text-white"
              key={option}
            >
              <input
                className="sr-only"
                type="radio"
                name="radius"
                defaultChecked={option === '500m'}
              />
              {option}
            </label>
          ))}
        </div>
      </fieldset>

      <button
        className="self-end rounded-md bg-teal-700 px-4 py-2 font-semibold text-white hover:bg-teal-800"
        type="button"
      >
        Apply filters
      </button>
    </section>
  );
}

function CandidateList() {
  return (
    <aside className="dashboard-list flex min-h-[360px] flex-col rounded-lg border border-zinc-200 bg-white shadow-sm">
      <div className="border-b border-zinc-200 p-4">
        <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
          Candidate locations
        </p>
        <h2 className="mt-1 text-lg font-semibold">Side List</h2>
      </div>

      <div className="space-y-3 p-4">
        <StateCard
          title="Loading state"
          tone="neutral"
          body="Skeleton rows appear here while candidate locations load."
        />
        <StateCard
          title="Error state"
          tone="warning"
          body="If locations fail to load, this panel will show retry guidance."
        />
        <StateCard
          title="Empty state"
          tone="empty"
          body="No candidate locations match the current filters yet."
        />
      </div>
    </aside>
  );
}

function MapWorkspace() {
  return (
    <section className="dashboard-map relative min-h-[420px] overflow-hidden rounded-lg border border-zinc-200 bg-white shadow-sm">
      <div className="absolute inset-x-0 top-0 z-10 flex flex-wrap items-center justify-between gap-3 border-b border-zinc-200 bg-white/95 p-4">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
            Map workspace
          </p>
          <h2 className="text-lg font-semibold">Map Area Placeholder</h2>
        </div>
        <div className="rounded-full bg-teal-50 px-3 py-1 text-sm font-semibold text-teal-800">
          Google Maps integration: future task
        </div>
      </div>

      <div className="h-full min-h-[420px] bg-[linear-gradient(90deg,#e4e4e7_1px,transparent_1px),linear-gradient(#e4e4e7_1px,transparent_1px)] bg-[size:44px_44px] pt-24">
        <div className="relative mx-auto h-[330px] max-w-3xl rounded-lg border border-dashed border-zinc-300 bg-zinc-50/80">
          <div className="absolute inset-0 flex items-center justify-center px-6 text-center">
            <div>
              <p className="text-xl font-semibold text-zinc-900">
                Candidate markers will render here
              </p>
              <p className="mt-2 max-w-md text-sm leading-6 text-zinc-600">
                This placeholder reserves the primary map canvas and keeps
                desktop and mobile layouts stable until the map UI task.
              </p>
            </div>
          </div>

          {mapPins.map((pin) => (
            <MapPin key={pin.id} pin={pin} />
          ))}
        </div>
      </div>
    </section>
  );
}

function MapPin({ pin }) {
  return (
    <div
      className="absolute z-10 -translate-x-1/2 -translate-y-1/2"
      style={{ top: pin.top, left: pin.left }}
      aria-label={`${pin.name} score ${pin.score}`}
    >
      <div className="grid h-10 w-10 place-items-center rounded-full border-2 border-white bg-teal-700 text-sm font-bold text-white shadow">
        {pin.score}
      </div>
    </div>
  );
}

function DetailPanel() {
  return (
    <aside className="dashboard-detail flex min-h-[360px] flex-col rounded-lg border border-zinc-200 bg-white shadow-sm">
      <div className="border-b border-zinc-200 p-4">
        <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
          Selected location
        </p>
        <h2 className="mt-1 text-lg font-semibold">Detail Panel Placeholder</h2>
      </div>

      <div className="flex flex-1 flex-col gap-4 p-4">
        <div className="rounded-md border border-zinc-200 bg-zinc-50 p-4">
          <p className="text-sm font-semibold text-zinc-900">
            Select a candidate location
          </p>
          <p className="mt-2 text-sm leading-6 text-zinc-600">
            Name, address, business type, total score, and last analyzed time
            will appear here after location data is connected.
          </p>
        </div>

        <dl className="grid grid-cols-2 gap-3 text-sm">
          <Metric label="Total score" value="--" />
          <Metric label="Last analyzed" value="--" />
          <Metric label="Nearby data" value="Pending" />
          <Metric label="Radius" value="500m" />
        </dl>
      </div>
    </aside>
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

function Metric({ label, value }) {
  return (
    <div className="rounded-md border border-zinc-200 bg-zinc-50 p-3">
      <dt className="text-xs font-medium uppercase tracking-wide text-zinc-500">
        {label}
      </dt>
      <dd className="mt-2 font-semibold text-zinc-950">{value}</dd>
    </div>
  );
}
