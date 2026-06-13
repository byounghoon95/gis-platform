import { useMutation } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { NavLink } from 'react-router-dom';

import { uploadCsvDataset, uploadDatasetOptions } from '../api/uploads';
import { useAuthStore } from '../auth/authStore';

export default function AdminUploadPage() {
  const [datasetType, setDatasetType] = useState(uploadDatasetOptions[0].value);
  const [file, setFile] = useState(null);
  const accessToken = useAuthStore((state) => state.accessToken);
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const selectedDataset = useMemo(
    () => uploadDatasetOptions.find((option) => option.value === datasetType),
    [datasetType],
  );

  const uploadMutation = useMutation({
    mutationFn: () => uploadCsvDataset({ datasetType, file, accessToken }),
  });

  const result = uploadMutation.data;
  const failedRows = result
    ? new Set(result.errors.map((error) => error.rowNumber)).size
    : 0;

  function handleSubmit(event) {
    event.preventDefault();

    if (!file) {
      return;
    }

    uploadMutation.mutate();
  }

  function handleDatasetChange(event) {
    setDatasetType(event.target.value);
    uploadMutation.reset();
  }

  function handleFileChange(event) {
    setFile(event.target.files?.[0] ?? null);
    uploadMutation.reset();
  }

  return (
    <main className="min-h-screen bg-zinc-100 text-zinc-950">
      <header className="border-b border-zinc-200 bg-white">
        <div className="dashboard-header mx-auto flex max-w-screen-xl flex-col gap-4 px-4 py-4 sm:px-6">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-teal-700">
              GIS Platform
            </p>
            <h1 className="mt-1 text-2xl font-semibold">Admin CSV Uploads</h1>
          </div>

          <nav className="flex flex-wrap items-center gap-2 text-sm font-medium">
            <NavLink
              to="/dashboard"
              className="rounded-md px-3 py-2 text-zinc-700 hover:bg-zinc-100"
            >
              Dashboard
            </NavLink>
            <NavLink
              to="/admin/uploads"
              className="rounded-md bg-zinc-950 px-3 py-2 text-white"
            >
              Admin uploads
            </NavLink>
            <span className="rounded-md border border-zinc-200 px-3 py-2 text-zinc-600">
              {user?.name ?? user?.email ?? 'Admin'}
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

      <div className="mx-auto grid max-w-screen-xl gap-4 px-4 py-4 sm:px-6 lg:grid-cols-[minmax(300px,380px)_minmax(0,1fr)]">
        <section className="rounded-lg border border-zinc-200 bg-white p-4 shadow-sm">
          <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
            Dataset import
          </p>
          <h2 className="mt-1 text-lg font-semibold">Upload CSV file</h2>

          <form className="mt-5 space-y-4" onSubmit={handleSubmit}>
            <label className="block text-sm font-medium text-zinc-700">
              Dataset type
              <select
                className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-zinc-950 outline-none focus:border-teal-700 focus:ring-2 focus:ring-teal-100"
                value={datasetType}
                onChange={handleDatasetChange}
              >
                {uploadDatasetOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </label>

            <div className="rounded-md border border-zinc-200 bg-zinc-50 p-3 text-sm leading-6 text-zinc-600">
              {selectedDataset?.helperText}
            </div>

            <label className="block text-sm font-medium text-zinc-700">
              CSV file
              <input
                className="mt-1 w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-950 file:mr-3 file:rounded-md file:border-0 file:bg-zinc-950 file:px-3 file:py-1.5 file:text-sm file:font-semibold file:text-white"
                type="file"
                accept=".csv,text/csv"
                onChange={handleFileChange}
              />
            </label>

            {uploadMutation.isError ? (
              <div className="rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-900">
                {uploadMutation.error.message}
              </div>
            ) : null}

            <button
              className="w-full rounded-md bg-teal-700 px-4 py-2 font-semibold text-white hover:bg-teal-800 disabled:cursor-not-allowed disabled:bg-zinc-400"
              type="submit"
              disabled={!file || uploadMutation.isPending}
            >
              {uploadMutation.isPending ? 'Uploading...' : 'Upload CSV'}
            </button>
          </form>
        </section>

        <section className="rounded-lg border border-zinc-200 bg-white shadow-sm">
          <div className="border-b border-zinc-200 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Validation result
            </p>
            <h2 className="mt-1 text-lg font-semibold">Import summary</h2>
          </div>

          {result ? (
            <div className="space-y-4 p-4">
              <dl className="grid gap-3 text-sm sm:grid-cols-3">
                <SummaryMetric label="Total rows" value={result.totalRows} />
                <SummaryMetric
                  label="Inserted rows"
                  value={result.insertedRows}
                />
                <SummaryMetric label="Failed rows" value={failedRows} />
              </dl>

              {failedRows > 0 ? (
                <ErrorTable errors={result.errors} />
              ) : (
                <div className="rounded-md border border-teal-200 bg-teal-50 p-4 text-sm font-medium text-teal-900">
                  Upload completed with no row-level validation errors.
                </div>
              )}
            </div>
          ) : (
            <div className="p-4">
              <div className="rounded-md border border-zinc-200 bg-zinc-50 p-4 text-sm leading-6 text-zinc-600">
                Choose a dataset and CSV file, then upload to review inserted
                rows and row-level validation messages.
              </div>
            </div>
          )}
        </section>
      </div>
    </main>
  );
}

function SummaryMetric({ label, value }) {
  return (
    <div className="rounded-md border border-zinc-200 bg-zinc-50 p-3">
      <dt className="text-xs font-medium uppercase tracking-wide text-zinc-500">
        {label}
      </dt>
      <dd className="mt-2 text-xl font-semibold text-zinc-950">{value}</dd>
    </div>
  );
}

function ErrorTable({ errors }) {
  return (
    <div className="overflow-hidden rounded-md border border-amber-200">
      <div className="border-b border-amber-200 bg-amber-50 px-4 py-3">
        <p className="text-sm font-semibold text-amber-950">
          Rows that need correction
        </p>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-zinc-200 text-left text-sm">
          <thead className="bg-zinc-50 text-xs uppercase tracking-wide text-zinc-500">
            <tr>
              <th className="px-4 py-3 font-semibold" scope="col">
                Row
              </th>
              <th className="px-4 py-3 font-semibold" scope="col">
                Field
              </th>
              <th className="px-4 py-3 font-semibold" scope="col">
                Message
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-200 bg-white text-zinc-700">
            {errors.map((error) => (
              <tr key={`${error.rowNumber}-${error.field}-${error.message}`}>
                <td className="whitespace-nowrap px-4 py-3 font-medium text-zinc-950">
                  {error.rowNumber}
                </td>
                <td className="whitespace-nowrap px-4 py-3">
                  {error.field || 'row'}
                </td>
                <td className="px-4 py-3">{error.message}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
